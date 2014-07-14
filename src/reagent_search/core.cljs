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
(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(println (foo "ed3-"))
(navbar/mount-navbar "navbar" @navbar-props)
(slogger/mount-logger "logger" @logger-props)
