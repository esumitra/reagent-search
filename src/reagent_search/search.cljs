(ns reagent-search.search
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <! timeout sliding-buffer]]
            [reagent.core :as reagent :refer [atom]]
            [reagent-search.utils :as utils]
            [reagent-search.solr :as solr]))

;;; autocomplete panel
(defn- autocomplete-item
  "renders a single autocomplete item in list"
  [chan-selecteditems item]
  [:li
   [:a {
        :href ""
        :on-click #(put! chan-selecteditems item)}
    item]])

(defn- handle-autocompleteitemselected
  "handles autocomplete item selected by
  1. logging selected item
  2. resetting the text input value
  3. removing the autocomplete panel"
  [chan-selecteditems ref-items ref-textvalue]
  (go-loop
   []
   (let [sel (<! chan-selecteditems)]
     (reset! ref-textvalue sel)
     (reset! ref-items []))
   (recur)))

(defn- autocomplete-component
  "renders the autocomplete component given a list of items to display
  and a selected item channel
  the number of items rendered is limited to size items"
  [size itemsref textvalue chan-selecteditems]
  (if-not (empty? @itemsref)
    (do
    (handle-autocompleteitemselected chan-selecteditems itemsref textvalue)
    [:div.open.dropdown-toggle {:data-toggle "dropdown"}
     [:ul.dropdown-menu
      {:role "menu"}
      (for [item (reverse (take size @itemsref))]
        ^{:key (utils/uuid)} [autocomplete-item chan-selecteditems item])]])
    [:div.close]))

;;; search input box and component
(defn- handle-querystream
  "gets autocomplete data from query"
  [chan-query ac-items]
  (go-loop
   []
   (let [q (<! chan-query)
         autocomplete-terms (<! (solr/get-autocomplete-terms q ac-items))]
     (reset! ac-items autocomplete-terms))
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

(defn- handle-keystream
  "responds to keyboard stream and handles the down,escape and return keys"
  [chan-keys chan-log textvalue autocomplete-items]
  (go-loop
   []
   (let [k (<! chan-keys)]
     (case k
       13 (do
            (println "enter pressed")
            (reset! autocomplete-items []))
       40 (do
            (println "keydown pressed"))
       nil))
   (recur)))

(defn- search-component
  "renders the search input box"
  [props]
  (let [chan-query (chan)
        chan-textvalue (chan)
        chan-keys (chan)
        chan-sampler (chan (sliding-buffer 1))
        textvalue (atom "")
        chan-log (:chan-log props)
        autocomplete-items (atom [])
        chan-selecteditems (chan)]
    (handle-textvaluestream chan-textvalue chan-sampler chan-log textvalue)
    (handle-samplerstream chan-sampler chan-query chan-log (:sample-timeout-ms props))
    (handle-querystream chan-query autocomplete-items)
    (handle-keystream chan-keys chan-log textvalue autocomplete-items)
    (fn[]
      [:div.input-group
       [:input.form-control
        {:type "text"
         :placeholder (:placeholder-text props)
         :value @textvalue
         :on-change #(put! chan-textvalue (-> % .-target .-value))
         :on-key-up #(put! chan-keys (-> % .-which))}]
       [:span.input-group-addon.glyphicon.glyphicon-search]
       [autocomplete-component (:autocomplete-size props) autocomplete-items textvalue chan-selecteditems]])))

(defn mount-search
  "mounts the search component with data props at dom-id"
  [dom-id props]
  (reagent/render-component
   [search-component props]
   (.getElementById js/document dom-id)))
