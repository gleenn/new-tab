(ns new-tab.events
  (:require [re-frame.core :as re-frame]
            [new-tab.db :refer [default-db links->local-store ls-key]]))

(defn allocate-next-id
  [links]
  (inc (apply max 0 (map :id links))))

;; this interceptor stores links into local storage
;; we attach it to each event handler which could update links
(def ->local-store (re-frame/after links->local-store))

;; Each event handler can have its own set of interceptors (middleware)
;; But we use the same set of interceptors for all event habdlers related
;; to manipulating links.
;; A chain of interceptors is a vector.
(def link-interceptors [;check-spec-interceptor               ;; ensure the spec is still valid
                        (re-frame/path :links)              ;; 1st param to handler will be the value from this path
                        ->local-store                       ;; write links to localstore
                        (re-frame/after #(.log js/console "after local store " %))
                        ;(when ^boolean js/goog.DEBUG debug)  ;; look in your browser console for debug logs
                        re-frame/trim-v])                   ;; removes first (event id) element from the event vec

(re-frame/reg-event-db
  :save-link
  link-interceptors
  (fn [links [title url]]
    (let [id (allocate-next-id links)]
      (conj links {:id id :title title :url url}))))

(re-frame/reg-cofx
  :local-store-links
  (fn [cofx _]
    "Read in todos from localstore, and process so we can merge into app-db."
    (assoc cofx :local-store-links
                (into [] (some->> (.getItem js/localStorage ls-key)
                                  (cljs.reader/read-string))))))

(re-frame/reg-event-fx                                      ;; on app startup, create initial state
  :initialize-db                                            ;; event id being handled
  [(re-frame/inject-cofx :local-store-links)]               ;; obtain links from localstore
  (fn [cofx]                                                ;; the handler being registered
    (let [db (:db cofx) local-store-links (:local-store-links cofx)]
      (do
        (.log js/console "cofx " cofx)
        (if (seq local-store-links)
          (do (.log js/console "links present " local-store-links) {:db (update-in default-db [:links] conj local-store-links)})
          (do (.log js/console "no links present") default-db)))))) ;; all hail the new state
