(defproject reagent-search "0.1.0-SNAPSHOT"
  :description "reagent search box"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2342"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [reagent "0.5.0-alpha"]
                 [cljs-ajax "0.2.6"]]

  :plugins [[lein-cljsbuild "1.0.3"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "reagent-search"
              :source-paths ["src"]
              :compiler {
                :output-to "reagent_search.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}
             {:id "reagent-search-min"
              :source-paths ["src"]
              :compiler {
                :output-to "reagent_search.min.js"
                :output-dir "out-min"
                :optimizations :advanced
                :source-map "reagent_search.min.js.map"}}]})
