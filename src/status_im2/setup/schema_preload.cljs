(ns status-im2.setup.schema-preload
  "Use this namespace to preload (require) namespaces that may directly or
  indirectly have instrumented vars. Don't require this namespace anywhere,
  leave it to Shadow CLJS to handle it.

  :dev/always is needed so that the compiler doesn't cache this file."
  {:dev/always true}
  (:require native-module.core
            quo2.core
            status-im2.common.log
            status-im2.events
            status-im2.navigation.core
            [status-im2.setup.schema :as schema]
            status-im2.subs.root
            utils.image-server))

(schema/setup!)
