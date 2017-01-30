(ns new-tab.db
  (:require [re-frame.core :as re-frame]))

(def default-db
  {:links [{:id 1 :title "Hacker News" :url "https://news.ycombinator.com"}]})

(def ls-key "links-reframe")

(defn links->local-store
  "Puts links into localStorage"
  [links]
  (do (.log js/console "links to local storage " links)
      (.setItem js/localStorage ls-key (str links))))
