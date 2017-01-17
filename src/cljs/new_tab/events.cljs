(ns new-tab.events
    (:require [re-frame.core :as re-frame]
              [new-tab.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))
