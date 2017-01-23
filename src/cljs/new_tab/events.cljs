(ns new-tab.events
  (:require [re-frame.core :as re-frame]
            [new-tab.db :as db]))

(defn allocate-next-id
  "Returns the next set id.
  Assumes set are sorted.
  Returns one more than the current largest id."
  [links]
  (do
    (.log js/console (str links))
    (inc (apply max 0 (map :id links)))))


(re-frame/reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/reg-event-db
  :save-link
  [(re-frame/path :links)]
  (fn [links [event text href]]
    (do (.log js/console (str "text " text) (str "href " href))
        (let [id (allocate-next-id links)]
          (conj links {:id id :text text :href href})))))
