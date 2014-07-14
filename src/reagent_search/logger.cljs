(ns reagent-search.logger
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <!]]
            [reagent.core :as reagent :refer [atom]]))

(defn- logger-component
  "renders a logger component that logs messages from a log channel
  :chan-log log channel
  :height height of log panel"
  [props]
  (let [logger (:chan-log props (chan))
        height (:height props "25px")]
    [:div.footer
     [:div.container
      [:div.panel.panel-default
       [:div.panel-body.logger
        [:p.text-muted "this is a log message"]]]]]))

(defn mount-logger
  "mounts the logger component with data props at dom-id"
  [dom-id props]
  (reagent/render-component
   [logger-component props]
   (.getElementById js/document dom-id)))
