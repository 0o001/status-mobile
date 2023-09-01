(ns quo2.components.wallet.transaction-summary.component-spec
  (:require [quo2.components.wallet.transaction-summary.view :as transaction-summary]
            [test-helpers.component :as h]))

(h/describe "Transaction summary"
  (h/test "default render"
    (h/render [transaction-summary/view props])
    (h/is-truthy (h/query-by-label-text :transaction-summary))))
