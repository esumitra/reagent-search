(ns reagent-search.simple
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <!]]
            [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def click-count (atom 0))

(defn simple-component
  []
  [:div
   [:p "I am a a component"]
   [:p
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} "and red "]
    "text"]])

(defn a-list
  [items]
  [:ul
   (for [item items]
     ^{:key item} [:li "Item " item])])

(defn couting-component
  []
  [:div
   "The atom " [:code "click-count"] "has value: " @click-count "."
   [:input {:type "button" :value "Click me!"
            :on-click #(swap! click-count inc)}]])

(defn atom-input [value]
  [:input {:type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])

(defn shared-state
  []
  (let [val (atom "foo")]
    (fn []
      [:div
       [:p "The value is now " @val]
       [:p "Change it here " [atom-input val]]])))

(defn simple-parent
  []
  [:div
    [:div
     [:p "I include a component"]
     [simple-component]]
    [:div
     [a-list (range 3)]]
    [:div
     [couting-component]]
    [:div
     [shared-state]]])

(defn mount-simple
  [dom-id]
  (reagent/render-component
   [simple-parent]
   (.getElementById js/document dom-id)))
