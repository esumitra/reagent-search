(ns reagent-search.logger
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <!]]
            [reagent.core :as reagent :refer [atom]]
            [reagent-search.utils :as utils]))



;; convenience logger functions
(defn info-message
  [chan-log msg]
  (put! chan-log {:type :info :text msg}))

(defn debug-message
  [chan-log msg]
  (put! chan-log {:type :debug :text msg}))

(defn error-message
  [chan-log msg]
  (put! chan-log {:type :error :text msg}))

(defn server-message
  [chan-log msg]
  (put! chan-log {:type :server :text msg}))

;; multimethods to display different message types differently
(defmulti display-message :type)

(defmethod display-message :info [msg]
  [:span.text-muted.bg-info (:text msg) [:br]])

(defmethod display-message :debug [msg]
  [:span.text-muted (:text msg) [:br]])

(defmethod display-message :error [msg]
  [:span.text-muted.bg-danger (:text msg) [:br]])

(defmethod display-message :server [msg]
  [:span.text-muted.bg-success (:text msg) [:br]])


;; logger has a local state that contains
;; all the messages logged so far
(defn- logger-component
  "renders a logger component that logs messages from a log channel
  :chan-log log channel
  :height height of log panel"
  [props]
  (let [logger (:chan-log props (chan))
        height (:height props "25px")
        messages (atom [{:type :info :text "messages will be logged here"}])]
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
            ^{:key (utils/uuid)} [display-message m])]]]])))

(defn mount-logger
  "mounts the logger component with data props at dom-id"
  [dom-id props]
  (reagent/render-component
   [logger-component props]
   (.getElementById js/document dom-id)))
