(ns reagent-search.search
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! chan <!]]
            [reagent.core :as reagent :refer [atom]]))

(defn- search-component
  "renders the search input box"
  [props]
  (let [chan-query (chan)]
    (fn[]
      [:div.input-group
       [:input.form-control
        {:type "text"
         :placeholder (:placeholder-text props)}]
       [:span.input-group-btn
        [:button.btn.btn-default
         {:type "button"}
         (:button-label props)]]]))
  )

(defn mount-search
  "mounts the search component with data props at dom-id"
  [dom-id props]
  (reagent/render-component
   [search-component props]
   (.getElementById js/document dom-id)))

