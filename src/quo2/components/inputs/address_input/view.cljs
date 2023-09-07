(ns quo2.components.inputs.address-input.view
  (:require [react-native.core :as rn]
            [react-native.clipboard :as clipboard]
            [quo2.theme :as quo.theme]
            [quo2.foundations.colors :as colors]
            [quo2.components.icon :as icon]
            [quo2.components.buttons.button.view :as button]
            [quo2.components.inputs.address-input.style :as style]
            [utils.i18n :as i18n]
            [reagent.core :as reagent]))

(def ^:private ens-regex #"^(?=.{5,255}$)([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.[a-zA-Z]{2,}$")

(defn- icon-color
  [blur? override-theme]
  (if blur?
    (colors/theme-colors colors/neutral-80-opa-30 colors/white-opa-10 override-theme)
    (colors/theme-colors colors/neutral-40 colors/neutral-60 override-theme)))

(defn- clear-button
  [{:keys [on-press blur? override-theme]}]
  [rn/touchable-opacity
   {:style    style/clear-icon-container
    :on-press on-press}
   [icon/icon :i/clear
    {:color (icon-color blur? override-theme)
     :size  20}]])

(defn- loading-icon
  [blur? override-theme]
  [rn/view {:style style/clear-icon-container}
   [icon/icon :i/loading
    {:color (icon-color blur? override-theme)
     :size  20}]])

(defn- positive-state-icon
  [override-theme]
  [rn/view {:style style/clear-icon-container}
   [icon/icon :i/positive-state
    {:color (colors/theme-colors colors/success-50 colors/success-60 override-theme)
     :size  20}]])

(defn- get-placeholder-text-color
  [status theme blur?]
  (cond
    (and (= status :default) blur?)
    (colors/theme-colors colors/neutral-80-opa-40 colors/white-opa-30 theme)
    (and (= status :default) (not blur?))
    (colors/theme-colors colors/neutral-40 colors/neutral-50 theme)
    (and (not= status :default) blur?)
    (colors/theme-colors colors/neutral-80-opa-20 colors/white-opa-20 theme)
    (and (not= status :default) (not blur?))
    (colors/theme-colors colors/neutral-30 colors/neutral-60 theme)))

(defn- f-address-input-internal
  []
  (let [status    (reagent/atom :default)
        value     (reagent/atom "")
        clipboard (reagent/atom nil)
        focused?  (atom false)]
    (fn [{:keys [scanned-value theme blur? on-change-text on-blur on-focus on-clear on-scan on-detect-ens
                 valid-ens?]}]
      (let [on-change              (fn [text]
                                     (let [ens? (boolean (re-matches ens-regex text))]
                                       (if (> (count text) 0)
                                         (reset! status :typing)
                                         (reset! status :active))
                                       (reset! value text)
                                       (when on-change-text
                                         (on-change-text text))
                                       (when (and ens? on-detect-ens)
                                         (reset! status :loading)
                                         (on-detect-ens text))))
            on-paste               (fn []
                                     (when-not (empty? @clipboard)
                                       (on-change @clipboard)
                                       (reset! value @clipboard)))
            on-clear               (fn []
                                     (reset! value "")
                                     (reset! status (if @focused? :active :default))
                                     (when on-clear
                                       (on-clear)))
            on-scan                #(when on-scan
                                      (on-scan))
            placeholder-text-color (get-placeholder-text-color @status theme blur?)]
        (rn/use-effect (fn []
                         (when scanned-value
                           (on-change scanned-value)
                           (reset! value scanned-value)))
                       [scanned-value])
        (clipboard/get-string #(reset! clipboard %))
        [rn/view {:style style/container}
         [rn/text-input
          {:accessibility-label    :address-text-input
           :style                  (style/input-text theme)
           :placeholder            (i18n/label :t/name-ens-or-address)
           :placeholder-text-color placeholder-text-color
           :default-value          @value
           :auto-complete          :none
           :auto-capitalize        :none
           :auto-correct           false
           :keyboard-appearance    (quo.theme/theme-value :light :dark theme)
           :on-focus               (fn []
                                     (when (= (count @value) 0)
                                       (reset! status :active))
                                     (reset! focused? true)
                                     (when on-focus (on-focus)))
           :on-blur                (fn []
                                     (when (= @status :active)
                                       (reset! status :default))
                                     (reset! focused? false)
                                     (when on-blur (on-blur)))
           :on-change-text         on-change}]

         (when (or (= @status :default)
                   (= @status :active))
           [rn/view {:style style/buttons-container}
            [button/button
             {:type            :outline
              :size            24
              :container-style {:margin-right 8}
              :inner-style     {:padding-top 1.5}
              :on-press        on-paste}
             (i18n/label :t/paste)]
            [button/button
             {:icon-only? true
              :type       :outline
              :size       24
              :on-press   on-scan}
             :main-icons/scan]])
         (when (= @status :typing)
           [rn/view {:style style/buttons-container}
            [clear-button
             {:on-press       on-clear
              :blur?          blur?
              :override-theme theme}]])
         (when (and (= @status :loading) (not valid-ens?))
           [rn/view {:style style/buttons-container}
            [loading-icon blur? theme]])
         (when (and (= @status :loading) valid-ens?)
           [rn/view {:style style/buttons-container}
            [positive-state-icon theme]])]))))

(defn address-input-internal
  [props]
  [:f> f-address-input-internal props])

(def address-input
  (quo.theme/with-theme address-input-internal))
