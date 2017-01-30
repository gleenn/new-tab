(ns new-tab.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.ratom :as ratom]
            [clojure.string :as str]))

(defn link-item [id text href]
  [:li {:key id} [:a {:href href} text]])

(defn make-input [props ratom on-save & placeholder]
  (let [input-value (ratom/atom ratom)]
    [:input {:type        "text"
             :value       @ratom
             :placeholder placeholder
             ;:on-blur     (on-save)
             :on-change   #(reset! ratom (-> % .-target .-value))
             :on-key-down #(case (.-which %)
                             13 (on-save)
                             27 (reset! ratom "")
                             nil)
             }]))

(defn inputs [{:keys [on-save]}]
  (let [title (ratom/atom "")
        url (ratom/atom "")
        save (fn [] (do
                      (.log js/console "save called " @title @url)
                      (on-save @title @url)
                      (reset! title)
                      (reset! url)))]
    (fn [props]
      [:div (make-input props title save "title")
       (make-input props url save "url")]
      )))

(defn main-panel []
  (let [links @(subscribe [:links])]
    (.log js/console (str "links " links))
    [:div
     [:ul (for [link links]
            (let [{:keys [id title url]} link]
              [link-item id title url]))]
     [inputs {:on-save (fn [title url] (dispatch [:save-link title url]))}]
     [:a {:on-click #(dispatch [:initialize-db]) :href "#"} "Reset"]]))
