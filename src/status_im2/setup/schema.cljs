(ns status-im2.setup.schema
  (:require [cljs.spec.alpha :as s]
            [cljs.spec.test.alpha :as spec.test]
            [expound.alpha :as expound]
            [malli.core :as malli]
            [malli.dev.cljs :as malli.dev]
            malli.dev.pretty
            malli.registry
            malli.util
            [status-im2.contexts.shell.schema :as shell.schema]))

(def ^:private ?unix-timestamp
  [:or zero? pos-int?])

(def ^:private ?public-key
  [:string {:min 1}])

(def ^:private ?style
  [:map-of :keyword [:or :int :string :keyword]])

(defn- core-schemas
  []
  {:s/unix-timestamp ?unix-timestamp
   :s/style          ?style
   :s/public-key     ?public-key})

(defn- registry
  "Application registry containing all available schemas. Each application context
  in status_im2/contexts/ should (optionally) expose a `schema/schemas`
  function."
  []
  (merge (malli/default-schemas)
         (malli.util/schemas)
         (core-schemas)
         (shell.schema/schemas)))

(defn setup!
  []
  ;; We need to use `malli.dev.pretty/thrower` instead of `malli.dev.pretty/report`, otherwise calls
  ;; to memoized functions won't fail on subsequent calls after the first failure.
  ;; (malli.dev/start! {:report (malli.pretty/thrower)})
  (malli.dev/start! {:report (malli.dev.pretty/reporter)})

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
