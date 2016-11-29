(ns hashobject.boot-s3.merge
  (:require [clojure.set :as s]))

(defn generate-deltas [local-file-details s3-file-details]
  (remove #(contains? s3-file-details
                      (select-keys % [:path :md5]))
          local-file-details))
