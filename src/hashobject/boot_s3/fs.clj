(ns hashobject.boot-s3.fs
  (:require [boot.core :as boot]
            [pandect.core :as p])
  (:import [java.io File]
           [java.util.regex Pattern]))

(declare relative-path)
(declare path->file-details)
(declare path->absolute-path)

(defn analyse-local-files
  "Analyse a local directory returnings a set
   of file details describing the relative path
   and md5 checksum of all the files (recursively)
   under the directory."
  [dir-path files]
  (->> files
       (remove #(.isDirectory (boot/tmp-file %)))
       (map (partial path->file-details dir-path))
       (set)))

(defn path->absolute-path [path]
  (.getAbsolutePath (clojure.java.io/file path)))

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
        file (boot/tmp-file tmp-file)
        md5 (p/md5 file)]
    {:path rel-path :md5 md5 :file file}))
