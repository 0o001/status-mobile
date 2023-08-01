(ns status-im2.setup.schema
  (:require [malli.core :as malli]
            [malli.dev.cljs :as malli.dev]
            malli.dev.pretty
            malli.dev.virhe
            malli.error
            malli.instrument
            malli.registry
            malli.util
            [status-im2.common.schema :as common.schema]
            [status-im2.contexts.shell.schema :as shell.schema]
            [taoensso.timbre :as log]))

(defn- printer
  []
  ;; Reduce the width from 80 to 60. This helps reading exceptions in small emulator screens.
  (malli.dev.pretty/-printer {:width 60}))

(defn- registry
  "Application registry containing all available schemas, i.e. keys in the map
  will be globally available.

  Since keys in a map are unique, remember to namespace keywords. Prefer to add
  to the global registry only schemas for domain entities (e.g. message, chat,
  notification, etc) or unambiguously useful schemas, like :s/unix-timestamp."
  []
  (merge (malli/default-schemas)
         (malli.util/schemas)
         (common.schema/schemas)
         (shell.schema/schemas)))

;; Custom explainer which is less verbose and compact than :malli.core/explain.
;; Importantly, sections "More information" and "Schema" are not shown.
(defmethod malli.dev.virhe/-format :status/malli-explain
  [_ _ explanation printer]
  {:body
   [:group
    [:group
     (malli.dev.virhe/-text "Value:" printer)
     :break
     [:align 2 (malli.dev.virhe/-visit (malli.error/error-value explanation printer) printer)]]
    :break
    :break
    [:group
     (malli.dev.virhe/-text "Errors:" printer)
     :break
     [:align 2 (malli.dev.virhe/-visit (malli.error/humanize explanation) printer)]]]})

(defn setup!
  "Configure Malli and initializes instrumentation.

  When instrumented vars are defined and hot reload hasn't been triggered, they
  won't be magically detected and instrumented. Such is the case when you bring
  up only the REPL for the shadow-cljs :test target, so you will need to
  manually call `setup!` once after defining a new instrumented var."
  []
  (malli.registry/set-default-registry! (registry))

  ;; In theory not necessary, but sometimes in a REPL session the dev needs to
  ;; call unstrument! manually.
  (malli.instrument/unstrument!)

  ;; We need to use `malli.dev.pretty/thrower` instead of `malli.dev.pretty/report`, otherwise calls
  ;; to memoized functions won't fail on subsequent calls after the first failure.
  (malli.dev/start! {:report (malli.dev.pretty/thrower (printer))})

  (log/info "Schemas successfully initialized."))
