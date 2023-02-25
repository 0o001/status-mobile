(ns quo2.components.selectors.disclaimer.view
  (:require [cljs.spec.alpha :as s]
            [quo2.components.markdown.text :as text]
            [quo2.components.selectors.disclaimer.style :as style]
            [quo2.components.selectors.selectors.view :as selectors]
            [react-native.core :as rn]))

(s/def ::on-change fn?)
(s/def ::accessibility-label keyword?)
(s/def ::props-style
  (s/nilable (s/map-of keyword?
                       (s/or :number  int?
                             :string  string?
                             :keyword keyword?))))
(s/def ::container-style ::props-style)
(s/def ::props
  (s/keys :opt-un [::on-change
                   ::accessibility-label
                   ::container-style]))

(s/fdef view
  :args (s/cat :props ::props
               :label string?))

(defn view
  [{:keys [checked? blur? accessibility-label container-style on-change]} label]
  [rn/touchable-without-feedback
   {:on-press            on-change
    :accessibility-label :disclaimer-touchable-opacity}
   [rn/view {:style (merge container-style (style/container blur?))}
    [selectors/checkbox
     {:accessibility-label accessibility-label
      :blur?               blur?
      :checked?            checked?
      :on-change           on-change}]
    [text/text
     {:size  :paragraph-2
      :style style/text}
     label]]])

;;; RCF

(comment
  ;; This is valid, the function behaves as usual.
  (view {:on-change identity :accessibility-label :whatever :container-style {}} "hello")

  ;; This throws an exception because the accessibility-label is a string
  ;; instead of a keyword.
  (view {:on-change identity :accessibility-label "whatever" :container-style {}} "hello"))
