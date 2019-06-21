(ns icfp-2019.task-test
  (:require [icfp-2019.task :as sut]
            [clojure.test :as t]))


;; This is a dumb test but it makes sure we can process
;; the initial problem files with no exceptions
(t/deftest test-file-parsing
  (doseq [num (range 1 150)]
    (let [r (format "part-1-initial/prob-%03d.desc" num)]
      (sut/resource->task r)
      (t/is (= 1 1)))))
