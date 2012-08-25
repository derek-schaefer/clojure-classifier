(ns clojure-classifier.classifier
  (:require [clojure-classifier.tokenizer :as tokenizer])
  (:gen-class))

(def wcount (atom {}))
(def ccount (atom {}))
(def probs (atom {}))

(defn train [lang category text]
  (swap! ccount assoc category (+ 1 (get @ccount category 0)))
  (doseq [w (tokenizer/each-word lang text)]
    (let [pair [category w]]
      (swap! wcount assoc pair (+ 1 (get @wcount pair 0))))))

(defn total-count [] (reduce + (vals @ccount)))

(declare word-prob)

(defn word-weighted-avg [category word]
  (let [weight 1.0
        assumed-prob 0.5
        basic-prob (word-prob category word)
        totals (reduce + (cons 0 (map #(get @wcount % 0) (keys @ccount))))]
    (/ (+ (* weight assumed-prob) (* totals basic-prob)) (+ weight totals))))

(defn document-prob [lang category text]
  (reduce * (cons 1.0 (map #(word-weighted-avg category %) (tokenizer/each-word lang text)))))

(defn text-prob [lang category text]
  (* (/ (get @ccount category 0) (total-count)) (document-prob lang category text)))

(defn category-scores [lang text]
  (doseq [c (seq @ccount)]
    (let [k (nth c 0)]
      (swap! probs assoc k (text-prob lang k text)))))

(defn classify [lang text]
  (category-scores lang text)
  (nth (first (reverse (sort-by second (seq @probs)))) 0))

(defn categories [] (keys @ccount))

(defn word-prob [category word]
  (let [wc (get @wcount [category word])]
    (if wc
      (/ wc (get @ccount category 1.0))
      0.0)))
