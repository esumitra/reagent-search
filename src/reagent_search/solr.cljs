(ns reagent-search.solr
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <! map<]]
            [reagent-search.utils :as utils]
            [reagent-search.logger :as slogger]))

(def solr-search-params
  {:q nil
   :limit 5
   :terms.prefix nil
   :terms.sort "count"
   :terms.fl "name"
   :wt "json"})

(def solr-autocomplete-url
  "http://localhost:8983/solr/collection1/terms")

(defn get-autocomplete-terms
  "returns a channel that will contain the autocomplete items for term from server"
  [term-query]
  (let [parms (merge solr-search-params {:q term-query :terms.prefix term-query})
        chan-result (utils/ajax-get solr-autocomplete-url parms)]
    (map< #(if (:success %)
                   (let [rj (.parse js/JSON (:response %))
                         terms-arr (into []
                            (.. rj -terms -name))]
                     (flatten (partition-all 1 2 terms-arr)))
                    ["error"])
          chan-result)))
