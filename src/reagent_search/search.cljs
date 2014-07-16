(ns reagent-search.search
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <!]]
            [reagent.core :as reagent :refer [atom]]))


(defn- handle-textvaluestream
  "handler for processing text value handle-textstream
  takes a value from the value stream and
  1. sets the textvalue ref to incoming value
  2. logs the incoming value"
  [chan-textvalue chan-log textvalue]
  (go-loop
   []
   (let [text (<! chan-textvalue)
         logtext (str "input text: " text)]
     (reset! textvalue text)
     (put! chan-log logtext)
     (recur))))

(defn- search-component
  "renders the search input box"
  [props]
  (let [chan-query (chan)
        chan-textvalue (chan)
        textvalue (atom "")
        chan-log (:chan-log props)]
    (handle-textvaluestream chan-textvalue chan-log textvalue)
    (fn[]
      [:div.input-group
       [:input.form-control
        {:type "text"
         :placeholder (:placeholder-text props)
         :value @textvalue
         :on-change #(put! chan-textvalue (-> % .-target .-value))}]
       [:span.input-group-btn
        [:button.btn.btn-default.glyphicon.glyphicon-search
         {:type "button"}
         #_(:button-label props)]]]))
  )

(defn mount-search
  "mounts the search component with data props at dom-id"
  [dom-id props]
  (reagent/render-component
   [search-component props]
   (.getElementById js/document dom-id)))

