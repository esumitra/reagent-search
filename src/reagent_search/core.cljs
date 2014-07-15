(ns reagent-search.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <!]]
            [reagent-search.simple :as simple]
            [reagent-search.navbar :as navbar]
            [reagent-search.logger :as slogger]))

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

(navbar/mount-navbar "navbar" @navbar-props)
(slogger/mount-logger "logger" @logger-props)

;; test logger
(put! (:chan-log @logger-props) (str "message: " (rand-int 100)))

;; test sub-menu
(swap! navbar-props
       update-in [:items 3 :items] conj {:name (str "Another Action" " " (rand-int 10)) :url "#"})
;; why does the logger update real-time but the menu does not?
;; should the state be passed as an atom if changes need to be rendered?
