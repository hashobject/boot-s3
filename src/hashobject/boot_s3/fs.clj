(ns hashobject.boot-s3.fs
  (:require [boot.core :as boot]
            [pandect.core :as p])
  (:import [java.io File]
           [java.util.regex Pattern]))

(declare relative-path)
(declare path->file-details)

(defn analyse-local-files
  "Analyse a local directory returnings a set
   of file details describing the relative path
   and md5 checksum of all the files (recursively)
   under the directory."
  [dir-path files]
  (->> files
       (map (partial path->file-details dir-path))
       set))

;; Private Helper Functions

(defn- root-path-regex [root]
  (let [updated-root (.replace root File/separator "/")]
    (str "^" (str updated-root "/"))))

(defn- relative-path [root target]
  (-> target
    (.replace File/separator "/")
    (.replaceAll (root-path-regex root) "")))

(defn- path->file-details [root-path tmp-file]
  (let [file-path (:path tmp-file)
        rel-path (relative-path root-path file-path)
        md5 (p/md5 (boot/tmp-file tmp-file))]
    {:path rel-path :md5 md5 :tmp-file tmp-file}))
