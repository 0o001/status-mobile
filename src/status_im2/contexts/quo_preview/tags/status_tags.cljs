(ns status-im2.contexts.quo-preview.tags.status-tags
  (:require [quo2.components.tags.status-tags :as quo-component]
            [reagent.core :as reagent]
            [status-im2.contexts.quo-preview.preview :as preview]))

(def status-tags-options
  {:label   "Status"
   :key     :status
   :type    :select
   :options [{:display "Positive"
              :value   {:type :positive}
              :key     :positive}
             {:display "Negative"
              :value   {:type :negative}
              :key     :negative}
             {:display "Pending"
              :value   {:type :pending}
              :key     :pending}]})

(defn preview-status-tags
  []
  (reagent/with-let [state (reagent/atom {:size   :small
                                          :blur?  false
                                          :theme  :light
                                          :status {:type :positive}
                                          :label  "Something"})]
    [preview/screen
     [preview/component
      {:component  quo-component/status-tag
       :state      state
       :descriptor (concat [status-tags-options]
                           (preview/generate-descriptor quo-component/?schema))}]]))
