(ns new-tab.db
  (:require [re-frame.core :as re-frame]))

(def default-db
  {:links []})

(def ls-key "links-reframe")

(defn links->local-store
  "Puts links into localStorage"
  [links]
  (.setItem js/localStorage ls-key (str links)))
