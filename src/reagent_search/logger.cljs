(ns reagent-search.logger
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <!]]
            [reagent.core :as reagent :refer [atom]]
            [reagent-search.utils :as utils]))

;; logger has a local state that contains
;; all the messages logged so far
(defn- logger-component
  "renders a logger component that logs messages from a log channel
  :chan-log log channel
  :height height of log panel"
  [props]
  (let [logger (:chan-log props (chan))
        height (:height props "25px")
        messages (atom ["messages will be logged here"])]
    ;; mount function to log messages in log channel
    (go (loop []
          (let [new-message (<! logger)]
            (println new-message)
            (swap! messages #(cons %2 %1) new-message))
          (recur)))
    (fn []
      [:div.footer
       [:div.container
        [:div.panel.panel-default
         [:div.panel-body.logger
          (for [m @messages]
            ^{:key (utils/uuid)} [:span.text-muted m [:br]])]]]])))

(defn mount-logger
  "mounts the logger component with data props at dom-id"
  [dom-id props]
  (reagent/render-component
   [logger-component props]
   (.getElementById js/document dom-id)))
