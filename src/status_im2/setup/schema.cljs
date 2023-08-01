(ns status-im2.setup.schema
  (:require [cljs.spec.alpha :as s]
            [cljs.spec.test.alpha :as spec.test]
            [expound.alpha :as expound]
            [malli.core :as malli]
            [malli.dev.cljs :as malli.dev]
            malli.dev.pretty
            malli.dev.virhe
            malli.error
            malli.registry
            malli.util
            [status-im2.common.schema :as common.schema]
            [status-im2.contexts.shell.schema :as shell.schema]))

(defn- printer
  []
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
  []
  ;; We need to use `malli.dev.pretty/thrower` instead of `malli.dev.pretty/report`, otherwise calls
  ;; to memoized functions won't fail on subsequent calls after the first failure.
  (malli.dev/start! {:report (malli.dev.pretty/thrower (printer))})

  ;; Enable human-friendly spec errors.
  (set! s/*explain-out*
    (expound/custom-printer
     {;; Don't print the "Relevant specs" section, that's too verbose.
      :print-specs?       false

      ;; Valid values can be too long and obscure the relevant error message.
      :show-valid-values? false}))

  ;; Instrument all spec'ed vars.
  ;;
  ;; Depending on the text editor being used, such as Emacs with CIDER, if you eval the entire file
  ;; (buffer) you'll need to manually call `instrument` again. There are known discussions about
  ;; this https://clojurians-log.clojureverse.org/cider/2020-06-18.
  (spec.test/instrument)

  (malli.registry/set-default-registry! (registry)))
