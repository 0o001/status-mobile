(ns utils.schema
  (:require [malli.core :as malli]
            [malli.dev.pretty :as malli.pretty]
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

(defn match
  "Returns `value` when it is a valid example of `?schema`, otherwise nil. This
  function is particularly useful when combined with when/if-let or in test
  assertions."
  [?schema value]
  (if (malli.pretty/explain ?schema value)
    nil
    value))
