(ns utils.schema
  (:require [malli.core :as malli]
            malli.dev.pretty
            malli.error
            malli.generator
            malli.util))

(defn generate-in
  "Same as `malli.generator/generate` but for a particular `path` in `?schema`.
  `path` is a vector, as in `get-in`.

  Usage:

    (let [?schema [:map
                   [:person
                    [:map [:age :int]]]]]
      (generate-in ?schema [:person :age]))
    ;; => 42
  "
  ([?schema path]
   (generate-in ?schema path nil))
  ([?schema path options]
   (-> ?schema
       malli/deref
       (malli.util/get-in path)
       (malli.generator/generate options))))

(defn explain
  "Validates `value` against `?schema` and pretty print errors. Just as
  malli.explain, returns nil when `value` is valid."
  [?schema value]
  (let [explainer  (fn []
                     (malli.error/with-error-messages
                      (malli/explain ?schema value)))
        prettifier (malli.dev.pretty/prettifier
                    :status/malli-explain
                    "Validation Error"
                    explainer
                    {})]
    (prettifier)))

(defn match
  "Returns `value` when it is a valid example of `?schema`, otherwise nil."
  [?schema value]
  (if (explain ?schema value)
    nil
    value))
