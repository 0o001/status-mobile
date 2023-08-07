(ns status-im2.setup.schema
  (:require [malli.core :as malli]
            [malli.dev.cljs :as malli.dev]
            [malli.dev.pretty :as malli.pretty]
            [malli.dev.virhe :as malli.virhe]
            malli.error
            malli.instrument
            malli.registry
            malli.util
            ;; [status-im2.db :as db]
            schema.common
            schema.fx
            schema.re-frame
            schema.shell
            utils.schema))

;;;; Formatters
;; These formatters replace the original ones provided by Malli. They are more
;; compact (less line breaks) and don't show the "More Information" section.

(defn block
  "Same as `malli.dev.pretty/-block`, but adds only one line break between `text`
  and `body`."
  [text body printer]
  [:group (malli.virhe/-text text printer) :break [:align 2 body]])

(defmethod malli.virhe/-format ::malli/explain
  [_ _ {:keys [schema] :as explanation} printer]
  {:body
   [:group
    (block "Value:" (malli.virhe/-visit (malli.error/error-value explanation printer) printer) printer)
    :break :break
    (block "Errors:" (malli.virhe/-visit (malli.error/humanize explanation) printer) printer)
    :break :break
    (block "Schema:" (malli.virhe/-visit schema printer) printer)]})

(defmethod malli.virhe/-format ::malli/invalid-input
  [_ _ {:keys [args input fn-name]} printer]
  {:body
   [:group
    (block "Invalid function arguments:" (malli.virhe/-visit args printer) printer)
    :break :break
    (block "Function Var:" (malli.virhe/-visit fn-name printer) printer)
    :break :break
    (block "Input Schema:" (malli.virhe/-visit input printer) printer)
    :break :break
    (block "Errors:" (malli.pretty/-explain input args printer) printer)]})

(defmethod malli.virhe/-format ::malli/invalid-output
  [_ _ {:keys [value args output fn-name]} printer]
  {:body
   [:group
    (block "Invalid function return value:" (malli.virhe/-visit value printer) printer)
    :break :break
    (block "Function Var:" (malli.virhe/-visit fn-name printer) printer)
    :break :break
    (block "Function arguments:" (malli.virhe/-visit args printer) printer)
    :break :break
    (block "Output Schema:" (malli.virhe/-visit output printer) printer)
    :break :break
    (block "Errors:" (malli.pretty/-explain output value printer) printer)]})

;; (defonce ^:private registry
;;   (atom (merge (malli/default-schemas)
;;                (malli.util/schemas)
;;                (common.schema/schemas))))

;; (defn- register!
;;   [schemas]
;;   (swap! registry merge schemas))

(defn- make-registry
  "Application registry containing all available schemas, i.e. keys in the map
  will be globally available.

  Since keys in a map are unique, remember to namespace keywords. Prefer to add
  to the global registry only schemas for domain entities (e.g. message, chat,
  notification, etc) or unambiguously useful schemas, like :schema.common/timestamp."
  []
  (merge (malli/default-schemas)
         (malli.util/schemas)
         schema.common/schemas
         schema.fx/schemas
         schema.re-frame/schemas
         schema.shell/schemas))

(defn setup!
  "Configure Malli and initializes instrumentation.

  After evaluating an s-exp in the REPL that changes a function schema you'll
  need to either save the file where the schema is defined and hot reload or
  manually call `setup!`, otherwise you won't see any changes. It is safe and
  even expected you will call `setup!` multiple times in REPLs."
  []
  (malli.registry/set-default-registry! (make-registry))
  ;; (malli.registry/set-default-registry! (malli.registry/mutable-registry registry))

  ;; (register! {:s/db db/?db})

  ;; In theory not necessary, but sometimes in a REPL session the dev needs to
  ;; call unstrument! manually.
  (malli.instrument/unstrument!)

  ;; We need to use `malli.dev.pretty/thrower` instead of `malli.dev.pretty/report`, otherwise calls
  ;; to memoized functions won't fail on subsequent calls after the first failure.
  (malli.dev/start! {:report (utils.schema/reporter)})

  (println "Schemas initialized."))
