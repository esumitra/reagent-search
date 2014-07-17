(ns reagent-search.solr
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <!]]
            [reagent-search.utils :as utils]))

(def solr-search-template
  {:q 1})

(def solr-search-url
  "http://localhost:8983/solr/collection1/terms?q=as&limit=5&terms.prefix=as&terms.sort=count&terms.fl=name&wt=json")
