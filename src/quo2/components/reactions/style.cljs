(ns quo2.components.reactions.style
  (:require [quo2.foundations.colors :as colors]))

(def reaction-styling
  {:flex-direction     :row
   :justify-content    :center
   :align-items        :center
   :padding-horizontal 8
   :border-radius      8
   :height             24})

(defn add-reaction
  [theme]
  (merge reaction-styling
         {:padding-horizontal 9
          :border-width       1
          :border-color       (colors/theme-colors colors/neutral-30 colors/neutral-70 theme)}))

(defn reaction
  [neutral? theme]
  (merge reaction-styling
         (cond->
           {:background-color (colors/theme-colors (if neutral?
                                                     colors/neutral-30
                                                     :transparent)
                                                   (if neutral?
                                                     colors/neutral-70
                                                     :transparent)
                                                   theme)}
           (and (= :dark theme) (not neutral?))
           (assoc :border-color colors/neutral-70
                  :border-width 1)
           (and (not (= :dark theme)) (not neutral?))
           (assoc :border-color colors/neutral-30
                  :border-width 1))))
