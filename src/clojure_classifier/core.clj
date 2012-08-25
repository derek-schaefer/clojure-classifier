(ns clojure-classifier.core
  (:require [clojure-classifier.classifier :as classifier])
  (:gen-class :main true))

(defn -main []
  (classifier/train "english" "cat1" "herp derp")
  (classifier/train "english" "cat2" "herp thingy")
  (classifier/train "english" "cat2" "herp stuff")
  (println (classifier/classify "english" "herp")))
