(ns reagent-search.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <!]]
            [reagent-search.simple :as simple]
            [reagent-search.navbar :as navbar]
            [reagent-search.logger :as slogger]
            [reagent-search.search :as search]))

(enable-console-print!)

(def navbar-props (atom
                   {:name "CLJS-React"
                    :url "#"
                    :items [{:name "Home" :url "#"}
                            {:name "Contacts" :url "#contacts"}
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
                    :placeholder-text "Type search here"
                    :button-label "Search"
                    :sample-timeout-ms 500}))

;; pass in navbar data as properties as the data and navbar is immutable
(navbar/mount-navbar "navbar" @navbar-props)

;; pass in logger data as properties since the log channel is ummutable
(slogger/mount-logger "logger" @logger-props)

(search/mount-search "search" @search-props)

;; test logger
#_(put! (:chan-log @logger-props) (str "message: " (rand-int 100)))

;; test sub-menu; will not work unless navbar is driven off local state since props are immutable
#_(swap! navbar-props
       update-in [:items 3 :items] conj {:name (str "Another Action" " " (rand-int 10)) :url "#"})

;; why does the logger update real-time but the menu does not?
;; since the navbar is rendered from react properties, the view does not change as properties are immutable
;; since the log panel messages are driven from react local state, modfying the state modifies the view
