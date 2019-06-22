(ns icfp-2019.task
  (:require [clojure.java.io :as io]
            [clojure.math.numeric-tower :as math]
            [clojure.string :as str]
            [instaparse.core :as insta]
            [clojure.java.io :as io])
  (:import java.awt.Polygon))

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

(defn new-task []
  (-> {}
      map->Task
      (assoc :obstacles [])
      (assoc :boosts [])))

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
    (reduce apply-node (new-task) task-ast)))

;; i.e. (resource->task "part-1-initial/prob-001.desc")
(defn resource->task [resource]
  (-> resource
      io/resource
      slurp
      read-task))

;; I don't want to lose the pure clj fields
;; so I'm adding fields as java polys for hit detection

;; utilities
;; TODO move someplace appropriate later

(defn distance [p1 p2]
  (let [x1 (first p1)
        x2 (first p2)
        y1 (second p1)
        y2 (second p2)]
    (math/sqrt (+ (math/expt (- x2 x1) 2)
                  (math/expt (- y2 y1) 2)))))


(defn find-closest [x y points]
  (apply min-key (partial distance [x y]) points))

(defn in-poly? [poly x y]
  (.contains poly x y))

(defn in-mine? [task x y]
  (in-poly? (:mine-poly task) x y))

(defn in-obstacle? [task x y]
t  (when (:obstacle-polys task)
      (every? true?
              (map #(in-poly? % x y) (:obstacle-polys task)))))

(defn is-open? [task x y]
  (and (in-mine? task x y)
       (not (in-obstacle? task x y))))

(defn task->open-floor
  "Given a task provides a list of points that are 'open floor'."
  [task]
  (let [bounding-box (.getBounds (:mine-poly task))
        height (.getHeight bounding-box)
        width (.getWidth bounding-box)]
    (filter #(is-open? task (first %) (second %))
            (for [x (range width)
                  y (range height)]
              [x y]))))


(defn create-polygon [point-list]
  (let [points (map vals point-list)
        polygon (Polygon.)]
    (doseq [p points]
      (.addPoint polygon (first p) (second p)))
    polygon))

(defn add-mine-poly [task]
  (assoc task :mine-poly (create-polygon (:mine task))))

(defn add-obstacle-poly [task ob]
  (let [polygon (create-polygon ob)]
    (assoc task :obstacle-polys (conj (:obstacle-polys task)
                                      polygon))))

(defn add-obstacle-polys [task]
  (let [obstacles (:obstacles task)]
    (reduce add-obstacle-poly task obstacles)))

(defn add-open-floor [task]
  (assoc task :open-floor (task->open-floor task)))

(defn task->poly-representation [task]
  (-> task
      add-mine-poly
      add-obstacle-polys
      add-open-floor))

(defn resource->full-poly [resource]
  (-> resource
      resource->task
      task->poly-representation))




