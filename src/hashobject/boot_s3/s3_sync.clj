(set-env!
  :dependencies '[[org.clojure/clojure "1.6.0"]])

(ns hashobject.boot-s3.s3-sync
  {:boot/export-tasks true}
  (:require [boot.core               :as boot]
            [boot.util               :as u]
            [hashobject.boot-s3.core :as s3]))


(def ^:private
  +defaults+ {:source "resources/public"
              :options {}})

(boot/deftask s3-sync
  "Sync local directory to AWS S3"
  [s source     PATH str "Source directory to upload to s3"
   b bucket     BUCKET str "s3 bucket name"
   a access-key ACCESS_KEY str "s3 access key"
   k secret-key SECRET str "s3 secret key"
   o options    OPTIONS {str str} "Extra option set for each s3 file"]
  (fn middleware [next-handler]
    (fn handler [fileset]
      (let [options (merge +defaults+ *opts*)
            cred (select-keys options [:access-key :secret-key])]
        (u/info "Start upload.\n")
        (s3/sync-to-s3 cred (:source options) (:bucket options) (:options options))
        (u/info "Uploaded.\n")))))
