(ns quo2.components.inputs.address-input.style
  (:require [quo2.foundations.colors :as colors]
            [quo2.components.markdown.text :as text]))

(def container
  {:padding-horizontal 20
   :padding-top        8
   :padding-bottom     16
   :height             48
   :width              "100%"
   :flex-direction     :row
   :align-items        :flex-start})

(def buttons-container
  {:flex-direction :row
   :align-items    :center})

(def clear-icon-container
  {:justify-content :center
   :align-items     :center
   :height          24
   :width           20})

(defn input-text
  [theme]
  (assoc (text/text-style {:size   :paragraph-1
                           :weight :monospace})
         :flex         1
         :color        (colors/theme-colors colors/neutral-100 colors/white theme)
         ;:padding-bottom 2
         :margin-top   0
         :margin-right 8
         :height       24))
