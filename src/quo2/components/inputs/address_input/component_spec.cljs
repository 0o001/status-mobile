(ns quo2.components.inputs.address-input.component-spec
  (:require [quo2.components.inputs.address-input.view :as address-input]
            [test-helpers.component :as h]
            [react-native.clipboard :as clipboard]))

(h/describe "Address input"
  (h/test "default render"
    (with-redefs [clipboard/get-string (fn [callback] #(callback ""))]
      (h/render [address-input/address-input])
      (h/is-truthy (h/query-by-label-text :address-text-input)))))
