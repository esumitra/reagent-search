(ns reagent-search.utils
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <!]]
            [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

;; see http://catamorphic.wordpress.com/2012/03/02/generating-a-random-uuid-in-clojurescript/
;; and http://en.wikipedia.org/wiki/Universally_unique_identifier#Version_4_.28random.29
(defn uuid
  "returns a type 4 random UUID: xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx"
  []
  (let [r (repeatedly 30 (fn [] (.toString (rand-int 16) 16)))]
    (apply str (concat (take 8 r) ["-"]
                       (take 4 (drop 8 r)) ["-4"]
                       (take 3 (drop 12 r)) ["-"]
                       [(.toString  (bit-or 0x8 (bit-and 0x3 (rand-int 15))) 16)]
                       (take 3 (drop 15 r)) ["-"]
                       (take 12 (drop 18 r))))))


(defn ajax-get
  "returns a channel with the result of the json request to uri with a map of pars parameters
  channel entries are of the form {:success true/false :response response/error-text}"
  [url pars]
  (let [out (chan)]
    (GET url
         {:params pars
          :handler #(put! out {:success true :response %})
          :error-handler #(put! out {:success false :response %2})})
    out))
