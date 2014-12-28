(ns reagent-search.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [put! chan <!]]
            [reagent-search.simple :as simple]
            [reagent-search.navbar :as navbar]
            [reagent-search.utils :as utils]
            [reagent-search.logger :as slogger]
            [reagent-search.search :as search]))

(enable-console-print!)

(def navbar-props (atom
                   {:name "CLJS-React"
                    :url "#"
                    :items [{:name "Home" :url "#"}
                            {:name "Wikipedia" :url "#contacts"}
                            {:name "Solr Search" :url "#search"}
                            {:name "Dropdown" :url "#"
                             :items [{:name "Action" :url "#"}
                                     {:name "Another Action" :url "#"}]}]}))

(def logger-props (atom {:chan-log (chan)
                         :height "125px"}))

(def search-props (atom
                   {:chan-keyboard (chan)
                    :chan-query (chan)
                    :chan-log (:chan-log @logger-props)
                    :placeholder-text "type search term here"
                    :button-label "Search"
                    :sample-timeout-ms 1000
                    :autocomplete-size 5}))

;; mount all components in DOM
(utils/mount-component navbar/navbar-component navbar-props "navbar")
(utils/mount-component slogger/logger-component @logger-props "logger")
(utils/mount-component search/search-component @search-props "search")

;; test sub-menu
;; (swap! navbar-props
;;       update-in [:items 3 :items] conj {:name (str "Another Action" " " (rand-int 100)) :url "#"})
