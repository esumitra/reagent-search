(ns reagent-search.utils
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <!]]
            [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

(enable-console-print!)

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
         {:headers {"Content-Type" "text/plain"}
          :params pars
          :handler #(put! out {:success true :response %})
          :error-handler #(put! out {:success false :response %2})})
    out))

(defn key-code
  "returns keycode for key event
  key codes:
  :u - up key
  :d - down key
  :enter - enter key
  :esc - escape key
  all other keys return escape values
  e.g., :on-key-up #(put! mychan (key-code %))"
  [key-event]
  (let [keycode (-> key-event .-which)]
    (condp = keycode
      38 :u
      40 :d
      13 :enter
      27 :esc
      keycode)))

(defn mount-component
  "mounts the component cmp with properties props at dom-id
  e.g., (mount-component maze1.logger/logger-coponent myprop \"logger\")"
  [cmp props dom-id]
  (reagent/render
    [cmp props]
    (.getElementById js/document dom-id)))

(defn open-new-tab
  "opens url in new tab"
  [url]
  (js/open url))

(defn open-wiki-page
  "opens wiki page in for term in new tab"
  [term]
  (let [esc-term (clojure.string/replace term #"\s" "_")
        url (str "http://en.wikipedia.org/wiki/" esc-term)]
    (println "url:" url)
    (open-new-tab url)))