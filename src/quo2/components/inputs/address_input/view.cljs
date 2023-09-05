(ns quo2.components.inputs.address-input.view
  (:require [react-native.core :as rn]
            [quo2.theme :as quo.theme]
            [quo2.components.markdown.text :as text]
            [quo2.foundations.colors :as colors]))

(defn- address-input-internal
  [{:keys [label container-style theme]} value]
  [rn/view {:style container-style}
   [text/text
    {:size   :paragraph-2
     :weight :medium
     :style  {:color (colors/theme-colors colors/neutral-50 colors/neutral-40 theme)}}
    label]])

(def address-input
  (quo.theme/with-theme address-input-internal))
