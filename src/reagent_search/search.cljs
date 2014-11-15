(ns reagent-search.search
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <! timeout sliding-buffer]]
            [reagent.core :as reagent :refer [atom]]
            [reagent-search.utils :as utils]
            [reagent-search.logger :as slogger]
            [reagent-search.solr :as solr]
            [reagent-search.wiki :as wiki]))

;;; autocomplete panel
(defn- autocomplete-item
  "renders a single autocomplete item in list"
  [chan-selecteditems chan-focus item focus-item]
  [:li
   [:a {
        :href ""
        :on-click #(put! chan-selecteditems item)}
    item]])

(defn- handle-autocompleteitemselected
  "handles autocomplete item selected by
  1. logging selected item
  2. resetting the text input value
  3. removing the autocomplete panel
  4. logs the selected component"
  [chan-selecteditems ref-items ref-textvalue chan-log]
  (go-loop
   []
   (let [sel (<! chan-selecteditems)]
     (reset! ref-textvalue sel)
     (reset! ref-items [])
     (slogger/info-message chan-log (str "selected value:" sel)))
   (recur)))

(defn- handle-autocomplete-focus
  "handles keyboard autocomplete focus events
  focus events are :u - up, :d down, all other events are ignored"
  [chan-focus ref-items ref-focus-item]
  (go-loop
   []
   (let [fevent (<! chan-focus)
         new-findex (condp = fevent
                      :u (dec @ref-focus-item)
                      :d (inc @ref-focus-item)
                      -1)
         new-fval (get (vec @ref-items) new-findex)]
     (println "items: " @ref-items)
     (println "new fval:" new-fval ", new-findex: " new-findex)
     (if (not (nil? new-fval))
       (reset! ref-focus-item new-findex))
     (recur))))

(defn- autocomplete-component
  "renders the autocomplete component given a list of items to display
  and a selected item channel
  the number of items rendered is limited to size items"
  [size itemsref textvalue chan-selecteditems chan-focus chan-log]
  (let [ref-focus-item (atom -1)]
        (handle-autocompleteitemselected chan-selecteditems itemsref textvalue chan-log)
        (handle-autocomplete-focus chan-focus itemsref ref-focus-item)
    (fn []
      (if-not (empty? @itemsref)
        (do
        ;; (handle-autocompleteitemselected chan-selecteditems itemsref textvalue)
        ;; (handle-autocomplete-focus chan-focus itemsref ref-focus-item)
          (reset! ref-focus-item -1)
        [:div.open.dropdown-toggle {:data-toggle "dropdown"}
         [:ul.dropdown-menu
          {:role "menu"}
          (for [item (take size @itemsref)]
            ^{:key (utils/uuid)} [autocomplete-item chan-selecteditems chan-focus item ref-focus-item])]])
        [:div.close]))))

;;; search input box and component
(defn- handle-querystream
  "gets autocomplete data from query"
  [chan-query chan-log ac-items]
  (go-loop
   []
   (let [q (<! chan-query)
         autocomplete-terms (<! (wiki/get-autocomplete-terms q))]
     (if (= ["error"] autocomplete-terms)
       (slogger/error-message chan-log "server error")
       (slogger/server-message chan-log (str autocomplete-terms)))
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
     (slogger/info-message chan-log logmsg)
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
     (slogger/debug-message chan-log logtext)
     (put! chan-sampler text)
     (recur))))

(defn- handle-keystream
  "responds to keyboard stream and handles the down,escape and return keys"
  [chan-keys chan-focus chan-log textvalue autocomplete-items]
  (go-loop
   []
   (let [k (<! chan-keys)]
     (case k
       13 (do
            (slogger/debug-message chan-log "enter key pressed")
            (reset! autocomplete-items []))
       40 (do
            (slogger/debug-message chan-log "down key pressed")
            (put! chan-focus :d))
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
        chan-selecteditems (chan)
        chan-focus (chan)]
    (handle-textvaluestream chan-textvalue chan-sampler chan-log textvalue)
    (handle-samplerstream chan-sampler chan-query chan-log (:sample-timeout-ms props))
    (handle-querystream chan-query chan-log autocomplete-items)
    (handle-keystream chan-keys chan-focus chan-log textvalue autocomplete-items)
    (fn[]
      [:div.input-group
       [:input.form-control
        {:type "text"
         :placeholder (:placeholder-text props)
         :value @textvalue
         :on-change #(put! chan-textvalue (-> % .-target .-value))
         :on-key-up #(put! chan-keys (-> % .-which))}]
       [:span.input-group-addon.glyphicon.glyphicon-search]
       [autocomplete-component (:autocomplete-size props) autocomplete-items textvalue chan-selecteditems chan-focus chan-log]])))

;; how to model behavior of search results?
;; typeahead component sends search query message to search-query channel
;; local handler listens to search-query-channel and updates local state
;; component localstate:
;; search-query - selected search query
;; search-results - list of results for search query
(defn- search-results
  "renders search results"
  [chan-search-query]
  (let [query-term (atom "No Search Query")
        query-result (atom [])]
    )
  )

(defn mount-search
  "mounts the search component with data props at dom-id"
  [dom-id props]
  (reagent/render-component
   [search-component props]
   (.getElementById js/document dom-id)))
