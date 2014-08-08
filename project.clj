(defproject reagent-search "0.1.0-SNAPSHOT"
  :description "reagent search box"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [reagent "0.4.2"]
                 [cljs-ajax "0.2.6"]]

  :plugins [[lein-cljsbuild "1.0.2"]]

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
                :source-map "maze1.min.js.map"}}]})
