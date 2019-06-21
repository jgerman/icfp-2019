(ns icfp-2019.task
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [instaparse.core :as insta]
            [clojure.java.io :as io]))

(def task-parser
  (insta/parser
   "<S> = MAP <'#'>
          START <'#'>
          OBSTACLE* <'#'>
          BOOSTS*
   MAP = POINTLIST
   START = POINT
   OBSTACLE = POINTLIST | POINTLIST <';'>*
   <BOOSTS> = BOOSTPOINT | BOOSTPOINT <';'> BOOSTS
   <POINTLIST> = POINT | POINT <','> POINTLIST
   POINT = <'('> NUM <','> NUM <')'>
   BOOSTPOINT = ['B'|'F'|'L'|'X'] POINT 
   <NUM> = #'[0-9]+'"))

(defrecord Point [x y])

(defn tree-node->Point [node]
  (let [x (read-string (nth node 1))
        y (read-string (nth node 2))]
    (->Point x y)))

(defrecord Boost [type
                  point])

(defn tree-node->Boost [node]
  (-> {}
      map->Boost
      (assoc :type (first node))
      (assoc :point (tree-node->Point (second node)))))

(defrecord Task [mine
                 start
                 obstacles
                 boosts])

(defmulti apply-node (fn [task node] (first node)))

(defmethod apply-node :MAP [task node]
  (assoc task :mine (map tree-node->Point(rest node))))

(defmethod apply-node :START [task node]
  (assoc task :start (tree-node->Point(second node))))

(defmethod apply-node :OBSTACLE [task node]
  (assoc task :obstacles (conj (:obstacles task)
                               (->> node
                                    rest
                                    (map tree-node->Point)))))

(defmethod apply-node :BOOSTPOINT [task node]
  (assoc task :boosts (conj (:boosts task)
                            (->> node
                                 rest
                                 tree-node->Boost))))

(defmethod apply-node :default [task node]
  (println "ERROR: " (first node)))

(defn read-task [task]
  (let [task-ast (task-parser task)]
    (reduce apply-node (map->Task {}) task-ast)))

(defn resource->task [resource]
  (-> resource
      io/resource
      slurp
      read-task))
