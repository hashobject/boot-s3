(ns boot-s3.merge
  (:require [clojure.set :as s]))

(defn generate-deltas [local-file-details s3-file-details]
  (let [upload-file-details (s/difference local-file-details s3-file-details)]
    (set (map #(vector :upload %) upload-file-details))))