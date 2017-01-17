(ns new-tab.views
  (:require [re-frame.core :as re-frame]))

(defn link-item [text href]
  [:li [:a {:href href} text]])

(defn main-panel []
  (let [links @(re-frame/subscribe [:links])]
    [:div
     (into [:ul] (map (fn [[text href]] (link-item text href)) links))]
    ))
