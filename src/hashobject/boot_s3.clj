(ns hashobject.boot-s3
  {:boot/export-tasks true}
  (:require [boot.core               :as boot]
            [boot.util               :as u]
            [hashobject.boot-s3.core :as s3]))


(def ^:private
  +defaults+ {:source "public"
              :options {:permissions [[:all-users :read]]}})

(boot/deftask s3-sync
  "Sync local directory to AWS S3

  The `options` parameter takes a map with two optional keys:
   - `:metadata` a map with metadata to set on the objects, passed through to clj-aws-s3
   - `:permissions` a seq of 2-tuples of `[grantee permission]`, passed through to clj-aws-s3"
  [s source     PATH       str       "subdirectory in :target-path to upload to s3"
   b bucket     BUCKET     str       "s3 bucket name"
   a access-key ACCESS_KEY str       "s3 access key"
   k secret-key SECRET     str       "s3 secret key"
   o options    OPTIONS    edn       "metadata and permissions to apply to all synced objects"]
  (let [options (merge +defaults+ *opts*)
        cred    (select-keys options [:access-key :secret-key])]
    (boot/with-post-wrap fileset
      (let [files (->> (boot/output-files fileset)
                       (boot/by-re [(re-pattern (str "^" (:source options)))]))]
        (u/info "Start upload to AWS S3.\n")
        (s3/sync-to-s3 cred files (:source options) (:bucket options) (:options options))
        (u/info "Uploaded to AWS S3.\n")))))
