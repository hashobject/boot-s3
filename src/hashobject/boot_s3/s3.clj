(ns hashobject.boot-s3.s3
  (:require [aws.sdk.s3 :as s3]))

(defn get-file-details-for
  "Get the file details for the file in s3.
   Returns nil if there is no file at the given key."
  [cred bucket-name key]
  (try
    (let [response (s3/get-object-metadata cred bucket-name key)]
      (assoc response :key key))
    (catch com.amazonaws.services.s3.model.AmazonS3Exception e
      (when-not (= 404 (.getStatusCode e))
        (throw e)))))

(defn- response->file-details [response]
  {:path (:key response) :md5 (:etag response)} )

(defn analyse-s3-bucket [cred bucket-name file-paths]
  (let [s3-lookup (partial get-file-details-for cred bucket-name)
        bucket-sync-state {:bucket-name bucket-name
                           :remote-file-details []}]
    (if-not (s3/bucket-exists? cred bucket-name)
      (assoc bucket-sync-state :errors [(str "No bucket " bucket-name)])
      (->> file-paths
           (map s3-lookup)
           (map response->file-details)
           (remove nil?)
           (set)))))

(defn make-file-public [cred bucket-name key]
  (let [grant (s3/grant :all-users :read)]
    (s3/update-object-acl cred bucket-name key grant)))

(defn put-file [cred bucket-name key file]
  (s3/put-object cred bucket-name key file))
