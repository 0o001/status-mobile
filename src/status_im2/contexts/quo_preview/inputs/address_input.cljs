(ns status-im2.contexts.quo-preview.inputs.address-input
  (:require [quo2.core :as quo]
            [reagent.core :as reagent]
            [status-im2.contexts.quo-preview.preview :as preview]))

(def descriptor
  [{:label "Scanned value:"
    :key   :scanned-value
    :type  :text}])

(defn view
  []
  (let [state (reagent/atom {:scanned-value ""})]
    (fn []
     [preview/preview-container {:state state :descriptor descriptor}
      [quo/address-input @state]])))
