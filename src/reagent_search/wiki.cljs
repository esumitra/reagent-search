(ns reagent-search.wiki
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <! map<]]
            [reagent-search.utils :as utils]
            [reagent-search.logger :as slogger]))

(def solr-search-params
  {:action "opensearch"
   :format "json"
   :search nil})

(def wiki-summary-params
  {:action "parse"
   :format "json"
   :section "0"
   :prop "text"
   :page nil})

(def wiki-autocomplete-url
  "http://en.wikipedia.org/w/api.php")

(def wiki-summary-url
  "http://en.wikipedia.org/w/api.php")

(defn get-autocomplete-terms
  "returns a channel that will contain the autocomplete items from wikipedia"
  [term-query]
  (let [parms (merge solr-search-params {:search term-query})
        chan-result (utils/ajax-get wiki-autocomplete-url parms)]
    (map< #(if (:success %)
             (let [rj (:response %)]
               (println "wiki-resp:" rj)
               (second rj))
             ["error"])
          chan-result)))

(defn get-term-summary
  "returns a channel that will contain the wiki summary for input term"
  [term]
  (let [parms (merge wiki-summary-params {:page term})
        chan-result (utils/ajax-get wiki-summary-url parms)]
    (map< #(if (:success %)
             (let [rj (:response %)
                   resp-map (js->clj rj)
                   content-html (get-in resp-map ["parse" "text" "*"])]
               content-html)
             "server error")
          chan-result)))
