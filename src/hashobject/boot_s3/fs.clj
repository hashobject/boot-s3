(ns hashobject.boot-s3.fs
  (:require [pandect.core :as p])
  (:import [java.io File]
           [java.util.regex Pattern]))

(declare relative-path)
(declare path->file-details)
(declare path->absolute-path)

(defn analyse-local-directory
  "Analyse a local directory returnings a set
   of file details describing the relative path
   and md5 checksum of all the files (recursively)
   under the directory."
  [dir-path]
  (let [root-dir (clojure.java.io/file dir-path)
        abs-dir-path (.getAbsolutePath root-dir)]
    (->> (file-seq root-dir)
         (filter #(not (.isDirectory %)))
         (map (partial path->file-details abs-dir-path))
         (set))))

(defn path->absolute-path [path]
  (.getAbsolutePath (clojure.java.io/file path)))

(defn combine-path [root-path rel-path]
  (let [root (clojure.java.io/file root-path)
        combined (clojure.java.io/file root rel-path)
        abs-path (.getAbsolutePath combined)]
    abs-path))

;; Private Helper Functions

(defn- root-path-regex [root]
  (let [updated-root (.replace root File/separator "/")]
    (str "^" (str updated-root "/"))))

(defn- relative-path [root target]
  (-> target
    (.replace File/separator "/")
    (.replaceAll (root-path-regex root) "")))

(defn- path->file-details [root-path file]
  (let [absolute-path (.getAbsolutePath file)
        rel-path (relative-path root-path absolute-path)
        md5 (p/md5-file absolute-path)]
    {:path rel-path :md5 md5}))