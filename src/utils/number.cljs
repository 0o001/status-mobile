(ns utils.number
  (:require [malli.core :as malli]
            malli.error
            malli.transform
            malli.util))

(defn naive-round
  "Quickly and naively round number `n` up to `decimal-places`.

  Example usage: use it to avoid re-renders caused by floating-point number
  changes in Reagent atoms. Such numbers can be rounded up to a certain number
  of `decimal-places` in order to avoid re-rendering due to tiny fractional
  changes.

  Don't use this function for arbitrary-precision arithmetic."
  [n decimal-places]
  (let [scale (Math/pow 10 decimal-places)]
    (/ (Math/round (* n scale))
       scale)))

(malli/=> naive-round
          [:=> [:cat :int :int] :int])

(defn parse-int
  "Parses `n` as an integer. Defaults to zero or `default` instead of NaN."
  ([n]
   (parse-int n 0))
  ([n default]
   (let [maybe-int (js/parseInt n 10)]
     (if (integer? maybe-int)
       maybe-int
       default))))

(defn value-in-range
  "Returns `num` if is in the range [`lower-bound` `upper-bound`]
  if `num` exceeds a given bound, then returns the bound exceeded."
  [number lower-bound upper-bound]
  (max lower-bound (min number upper-bound)))

;;; RCF
;;;; Instrumentation

(comment
  (naive-round "10" "1")
  ;; => Should throw an error
)

;;;; Validation

(comment
  (malli/validate [:string {:min 1}]
                  "h")
  ;; => true

  (malli/validate (malli/from-ast {:type :string})
                  "h")
  ;; => true

  (malli/validate [:qualified-keyword {:namespace :t}]
                  :t/some-translation)
  ;; => true

  (malli/validate [:qualified-keyword {:namespace :t}]
                  :some-translation)
  ;; => false
)

;;;; Map syntax

(comment
  (malli/ast [:map
              [:id string?]
              [:tags [:set keyword?]]
              [:address
               [:map
                [:street string?]
                [:city string?]
                [:zip int?]
                [:lonlat [:tuple double? double?]]]]])
  ;; => {:type :map,
  ;;     :keys
  ;;     {:id {:order 0, :value {:type string?}},
  ;;      :tags {:order 1, :value {:type :set, :child {:type keyword?}}}, :address
  ;;      {:order 2,
  ;;       :value
  ;;       {:type :map,
  ;;        :keys
  ;;        {:street {:order 0, :value {:type string?}},
  ;;         :city {:order 1, :value {:type string?}},
  ;;         :zip {:order 2, :value {:type int?}},
  ;;         :lonlat
  ;;         {:order 3,
  ;;          :value {:type :tuple, :children [{:type double?} {:type double?}]}}}}}}}
)

;;;; Decode/encode & coercion

(comment
  (malli/decode [:map
                 [:id string?]
                 [:type [:enum :kikka :kukka]]]
                {:id   "0x"
                 :type "kukka"}
                malli.transform/string-transformer)
  ;; => {:id "0x", :type :kukka}
)

;;;; Transforming schemata

(comment
  (malli/validate (malli/form (malli.util/merge :string :int))
                  "hey")
  ;; => false

  (malli/validate (malli/form (malli.util/merge :string :int))
                  100)
  ;; => true

  (malli/validate (malli/form (malli.util/merge [:map
                                                 [:id [:string {:min 1}]]
                                                 [:age {:optional true} [:int]]]
                                                [:map
                                                 [:age [:maybe :int]]]))
                  {:id  "0x1"
                   :age nil})
  ;; => true

  (malli.error/humanize
   (malli/explain
    (malli/form
     (malli.util/select-keys
      [:map {:closed true}
       [:id [:string {:min 1}]]
       [:age {:optional true} [:int]]]
      [:id]))
    {:id  "0x1"
     :age 10}))
  ;; => {:age ["disallowed key"]}
)

;;;; Multi schemata

(comment

)
