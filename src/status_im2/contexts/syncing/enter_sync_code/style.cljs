(ns status-im2.contexts.syncing.enter-sync-code.style
  (:require [quo2.foundations.colors :as colors]
            [quo2.foundations.typography :as typography]
            [react-native.platform :as platform]))

(def container-text-input
  {:flex-direction     :row
   :justify-content    :space-between
   :padding-horizontal 20})

(defn text-input-container
  [invalid?]
  {:padding-top    1
   :padding-left   12
   :padding-right  7
   :padding-bottom 7
   :flex           1
   :flex-direction :row
   :border-width   1
   :border-radius  12
   :border-color   (if invalid?
                     colors/danger-50-opa-40
                     colors/neutral-60)})

(defn text-input
  []
  (merge typography/monospace
         typography/paragraph-1
         {:flex       1
          :margin-top (if platform/android?
                        4
                        0)
          :padding    0
          :color      colors/white}))

(def label-texts-container
  {:flex-direction :row
   :height         18
   :margin-bottom  8})

(def button-paste
  {:margin-top 8})

(def clear-icon
  {:size  20
   :color colors/neutral-80-opa-30})

(def right-icon-touchable-area
  {:margin-left   8
   :padding-right 4
   :padding-top   6
   :margin-bottom 4})

(def label-pairing
  {:color colors/white-opa-40})

(def label-container
  {:flex-direction :row
   :margin-left    20
   :line-height    18
   :margin-top     20
   :margin-bottom  8})

(def continue-button-container
  {:margin-top         12
   :padding-horizontal 22})
