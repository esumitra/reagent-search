(ns reagent-search.navbar
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <!]]
            [reagent.core :as reagent :refer [atom]]))


(defn- menu-item
  "top level navbar menu item
  nested items are rendered as sub-items
  only two levels of menu nesting is supported"
  [item]
  (cond
   (nil? (:items item))
   [:li
    [:a {:href (:url item)}
     (:name item)]]

   :else
   [:li {:className "dropdown"}
    [:a {:href "#"
         :className "dropdown-toggle"
         :data-toggle "dropdown"}
     (:name item)
     [:span {:className "caret"}]]
    [:ul {:className "dropdown-menu"
          :role "menu"}
     (for [sub-item (:items item)]
       ^{:key (:name sub-item)} [menu-item sub-item])]]))

(defn- menu-items-container
  "menu items container with menu items"
  [menu-items]
  (if-let [items menu-items]
    [:div.collapse.navbar-collapse
     [:ul.nav.navbar-nav
      (for [item items]
        ^{:key (:name item)} [menu-item item])]]))

(defn navbar-component
  "renders a navbar for input props"
  [props]
  [:div {:className "navbar navbar-default navbar-fixed-top"
         :role "navigation"}
   [:div.container
    [:div.navbar-header
     [:button.navbar-toggle {:type "button"
                             :data-toggle "collapse"
                             :data-target ".navbar-collapse"}
      [:span.sr-only "toggle navigation"]
      [:span.icon-bar]
      [:span.icon-bar]
      [:span.icon-bar]]
     [:a.navbar-brand {:href (:url @props)}
      (:name props)]]
    [menu-items-container (:items @props)]]])
