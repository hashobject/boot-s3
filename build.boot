(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[org.clojure/clojure "1.6.0"]
                  [adzerk/bootlaces "0.1.9" :scope "test"]
                  [clj-aws-s3 "0.3.10"]
                  [pandect "0.5.2"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.1.0-SNAPSHOT")
(bootlaces! +version+)

(task-options!
  pom {:project 'hashobject/boot-s3
       :version +version+
       :description "Boot task for syncing a local directory with AWS S3 bucket"
       :url         "https://github.com/hashobject/boot-s3"
       :scm         {:url "https://github.com/hashobject/boot-s3"}
       :license     {"name" "Eclipse Public License"
                     "url"  "http://www.eclipse.org/legal/epl-v10.html"}})


(deftask install-locally
  "Install locally"
  []
  (comp (pom) (jar) (install)))

(deftask release-snapshot
  "Release snapshot"
  []
  (comp (pom) (jar) (push-snapshot)))