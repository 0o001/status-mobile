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

(defn reporter
  "Custom reporter optimized for small screens."
  []
  (malli.pretty/reporter
   (malli.pretty/-printer
    {:width        60
     :print-length 6
     :print-level  3
     :print-meta   false})))

(defn =>
  "Similar to `malli/=>`, but can instrument functional Reagent components and
  anonymous functions.

  Different from `malli/=>`, it is less verbose because there's no need to
  surround the schema with `[:=> ...]`, also the output schema is optional."
  ([?input f]
   (=> ?input nil f))
  ([?input ?output f]
   (malli/-instrument {:schema (if ?output
                                 [:=> ?input ?output]
                                 [:=> ?input :any])
                       :scope  (if ?output
                                 #{:input :output}
                                 #{:input})
                       :report (reporter)}
                      f)))
