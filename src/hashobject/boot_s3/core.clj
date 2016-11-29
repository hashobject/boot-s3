(ns hashobject.boot-s3.core
  (:require [hashobject.boot-s3.fs :as fs]
            [hashobject.boot-s3.s3 :as s3]
            [hashobject.boot-s3.merge :as m]))

(declare capture-file-details)
(declare calculate-deltas)
(declare push-deltas-to-s3)

(declare print-delta-summary)
(declare print-sync-complete-message)

(def default-options {:public true})

(defn sync-to-s3
  "Syncronise the local directory 'dir-path' to the S3 bucket 'bucket-name'."
  ([aws-credentials files dir-path bucket-name]
   (sync-to-s3 aws-credentials dir-path bucket-name {}))
  ([aws-credentials files dir-path bucket-name options]
   (let [sync-state {:aws-credentials aws-credentials
                     :files files
                     :dir-path dir-path
                     :bucket-name bucket-name
                     :options (merge default-options options)}]
      (-> sync-state
        (capture-file-details)
        (calculate-deltas)
        (print-delta-summary)
        (push-deltas-to-s3)
        (print-sync-complete-message)))))

;; Private functions

(def padding (apply str (take 30 (repeat " "))))

(defn- capture-file-details
  "Pull the local directories file details and the S3 buckets file details
   and associate them with the sync-state."
  [{:keys [aws-credentials dir-path files bucket-name] :as sync-state}]
  (let [local-file-details (fs/analyse-local-files dir-path files)
        file-paths (map :path local-file-details)
        remote-file-details (s3/analyse-s3-bucket aws-credentials bucket-name file-paths)]
    (merge sync-state {:local-file-details local-file-details
                       :remote-file-details remote-file-details})))

(defn- calculate-deltas
  "Based on the local file details and the remote file details, calculate
   which local files need to be pushed and which do not."
  [{:keys [errors local-file-details remote-file-details] :as sync-state}]
  (if (empty? errors)
    (let [deltas (m/generate-deltas local-file-details remote-file-details)]
      (assoc sync-state :deltas deltas))))

(defn- push-deltas-to-s3
  "Pushes the local files named in the delta list to S3."
  [{:keys [errors aws-credentials bucket-name deltas options] :as sync-state}]
  (when (empty? errors)
    (loop [deltas deltas]
      (if (not (empty? deltas))
        (let [[_ {:keys [path file]}] (first deltas)]
          (print "  " path "uploading ...")

          (s3/put-file
            aws-credentials
            bucket-name
            path
            file)

          (when (:public options)
            (s3/make-file-public aws-credentials bucket-name path))

          (println "\r  " path "done." padding)
          (recur (rest deltas))))))
  sync-state)

;; Print Functions

(defn- print-delta-summary [{:keys [errors deltas] :as sync-state}]
  (cond
    (not (empty? errors)) nil
    (empty? deltas) (println "\rThere are no local changes to push." padding)
    (= 1 (count deltas)) (println "\nThere is 1 local file change to upload:")
    :default (println "\nThere are" (count deltas)  "local file changes to upload:"))
  sync-state)

(defn- print-sync-complete-message [{:keys [errors deltas]}]
  (cond
    (not (empty? errors)) (println (str "\r"  (first errors) padding))
    (not (empty? deltas)) (println "Sync complete.")))
