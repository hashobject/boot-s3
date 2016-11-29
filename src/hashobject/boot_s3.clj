(ns hashobject.boot-s3
  {:boot/export-tasks true}
  (:require [boot.core               :as boot]
            [boot.util               :as u]
            [hashobject.boot-s3.core :as s3]))


(def ^:private
  +defaults+ {:source "public"
              :permissions [[:all-users :read]]})

(boot/deftask s3-sync
  "Sync local directory to AWS S3"
  [s source      PATH       str       "subdirectory in :target-path to upload to s3"
   b bucket      BUCKET     str       "s3 bucket name"
   a access-key  ACCESS_KEY str       "s3 access key"
   k secret-key  SECRET     str       "s3 secret key"
   m metadata    META       {kw str}  "a map with metadata to set on the objects, passed through to clj-aws-s3"
   p permissions PERMS      [[kw kw]] "a seq of 2-tuples of `[grantee permission]`, passed through to clj-aws-s3"
   f force                  bool      "Set to `true` to force upload of all objects"]
  (let [options (merge +defaults+ *opts*)
        cred    (select-keys options [:access-key :secret-key])
        s3-opts {:metadata metadata :permissions (:permissions options)}]
    (boot/with-post-wrap fileset
      (let [files (->> (boot/output-files fileset)
                       (boot/by-re [(re-pattern (str "^" (:source options)))]))]
        (u/info "Start upload to AWS S3.\n")
        (s3/sync-to-s3 cred files (:source options) (:bucket options) s3-opts force)
        (u/info "Uploaded to AWS S3.\n")))))
