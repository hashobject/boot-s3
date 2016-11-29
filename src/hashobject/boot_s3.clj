(ns hashobject.boot-s3
  {:boot/export-tasks true}
  (:require [boot.core               :as boot]
            [boot.util               :as u]
            [hashobject.boot-s3.core :as s3]))


(def ^:private
  +defaults+ {:source "public"
              :options {}})

(boot/deftask s3-sync
  "Sync local directory to AWS S3"
  [s source     PATH       str       "subdirectory in :target-path to upload to s3"
   b bucket     BUCKET     str       "s3 bucket name"
   a access-key ACCESS_KEY str       "s3 access key"
   k secret-key SECRET     str       "s3 secret key"
   o options    OPTIONS    {str str} "Extra options set for each s3 file"
   f force                 bool      "Set to `true` to force upload of all objects"]
  (let [options (merge +defaults+ *opts*)
        cred    (select-keys options [:access-key :secret-key])]
    (boot/with-post-wrap fileset
      (let [files (->> (boot/output-files fileset)
                       (boot/by-re [(re-pattern (str "^" (:source options)))]))]
        (u/info "Start upload to AWS S3.\n")
        (s3/sync-to-s3 cred files (:source options) (:bucket options) (:options options) force)
        (u/info "Uploaded to AWS S3.\n")))))
