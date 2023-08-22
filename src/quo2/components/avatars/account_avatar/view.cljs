(ns quo2.components.avatars.account-avatar.view
  (:require [clojure.string :as string]
            [quo2.components.avatars.account-avatar.style :as style]
            [quo2.foundations.twemoji :as twemoji]
            [quo2.theme :as quo.theme]
            [react-native.core :as rn]))

(def default-emoji "🍑")

(defn- view-internal
  "Opts:
   
    :type  - keyword -> :default/:watch-only

    :emoji - string -> 🍑 [default]
   
    :size  - number -> 80 [default] /48/32/28/24/20/16
   
    :customization-color - keyword or hexstring -> :blue/:army/... or #ABCEDF
   
    :theme - keyword -> :light/:dark"
  [{:keys [size emoji]
    :or   {size  style/default-size
           emoji default-emoji}
    :as   opts}]
  (let [emoji-size (style/get-emoji-size size)]
    [rn/view
     {:accessible          true
      :accessibility-label :account-avatar
      :style               (style/root-container opts)}
     [twemoji/twemoji
      {:style {:font-size emoji-size}}
      (string/trim emoji)]]))

(def view (quo.theme/with-theme view-internal))
