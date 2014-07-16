(ns reagent-search.search
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <! timeout sliding-buffer]]
            [reagent.core :as reagent :refer [atom]]
            [reagent-search.utils :as utils]))

;;; autocomplete panel

(defn- autocomplete-item
  "renders a single autocomplete item in list"
  [item]
  [:li
   [:a {:href ""} item]])

(defn- autocomplete-component
  "renders the autocomplete component given a list of items to display
  and a selected item channel
  the number of items rendered is limited to size items"
  [size itemsref chan-selecteditems]
  (let [items itemsref]
  (fn []
  (if-not (empty? @items)
    [:div.open.dropdown-toggle {:data-toggle "dropdown"}
     [:ul.dropdown-menu
      {:role "menu"}
      (for [item (reverse (take size @items))]
        ^{:key (utils/uuid)} [autocomplete-item item])]]
    [:div.close]))))

;;; search input box and component
(defn- handle-querystream
  "gets autocomplete data from query"
  [chan-query ac-items]
  (go-loop
   []
   (let [q (<! chan-query)]
     (println "query: " q)
     (swap! ac-items #(cons %2 %1) q))
   (recur)))

(defn- handle-samplerstream
  "samples the input from sampler channel and pushes data at sampled intervals into query and log channels"
  [chan-sampler chan-query chan-log timeoutms]
  (go-loop
   []
   (<! (timeout timeoutms))
   (let [sampled-text (<! chan-sampler)
         logmsg (str "sampled text: " sampled-text)]
     (put! chan-log logmsg)
     (put! chan-query sampled-text))
   (recur)))

(defn- handle-textvaluestream
  "handler for processing text value handle-textstream
  takes a value from the value stream and
  1. sets the textvalue ref to incoming value
  2. logs the incoming value"
  [chan-textvalue chan-sampler chan-log textvalue]
  (go-loop
   []
   (let [text (<! chan-textvalue)
         logtext (str "input text: " text)]
     (reset! textvalue text)
     (put! chan-log logtext)
     (put! chan-sampler text)
     (recur))))

(defn- search-component
  "renders the search input box"
  [props]
  (let [chan-query (chan)
        chan-textvalue (chan)
        chan-sampler (chan (sliding-buffer 1))
        textvalue (atom "")
        chan-log (:chan-log props)
        autocomplete-items (atom [])
        chan-selecteditems (chan)]
    (handle-textvaluestream chan-textvalue chan-sampler chan-log textvalue)
    (handle-samplerstream chan-sampler chan-query chan-log (:sample-timeout-ms props))
    (handle-querystream chan-query autocomplete-items)
    (fn[]
      [:div.input-group
       [:input.form-control
        {:type "text"
         :placeholder (:placeholder-text props)
         :value @textvalue
         :on-change #(put! chan-textvalue (-> % .-target .-value))}]
       [:span.input-group-addon.glyphicon.glyphicon-search]
       [autocomplete-component (:autocomplete-size props) autocomplete-items chan-selecteditems]])))

(defn mount-search
  "mounts the search component with data props at dom-id"
  [dom-id props]
  (reagent/render-component
   [search-component props]
   (.getElementById js/document dom-id)))
