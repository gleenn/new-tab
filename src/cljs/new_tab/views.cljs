(ns new-tab.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.ratom :as ratom]
            [clojure.string :as str]))

(defn link-item [text href]
  [:li [:a {:href href} text]])


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
  (let [text (ratom/atom "")
        href (ratom/atom "")
        save (fn [] (do
                      (when (and (not (str/blank? @text))
                                 (not (str/blank? @href)))
                        (.log js/console "save called " @text @href)
                        (on-save @text @href)
                        (reset! text)
                        (reset! href))))]
    (fn [props]
      [:div (make-input props text save "title")
            (make-input props href save "url")]
      )))

(defn main-panel []
  (let [links @(subscribe [:links])]
    [:div
     [:ul (for [link links]
            ^{:key (:id link)} [link-item (:text link) (:href link)])]
     [inputs {:on-save (fn [text href] (dispatch [:save-link text href]))}]
     [:a {:on-click #(dispatch [:initialize-db]) :href "#"} "Reset"]]))
