(ns status-im2.contexts.onboarding.sign-in.view
  (:require [utils.i18n :as i18n]
            [status-im2.contexts.syncing.scan-sync-code.view :as scan-sync-code]
            [reagent.core :as reagent]))

(defn sign-in-view
  []
  [scan-sync-code/view
   {:title             (i18n/label :t/sign-in-by-syncing)
    :show-bottom-view? true
    :background        [:<>]}])

(def view (reagent/reactify-component sign-in-view))
