(ns status-im.data-store.realm.schemas.account.v1.chat
  (:require [taoensso.timbre :as log]
            [status-im.ui.components.styles :refer [default-chat-color]]))

(def schema {:name       :chat
             :primaryKey :chat-id
             :properties {:chat-id          :string
                          :name             :string
                          :color            {:type    :string
                                             :default default-chat-color}
                          :group-chat       {:type    :bool
                                             :indexed true}
                          :group-admin      {:type     :string
                                             :optional true}
                          :is-active        :bool
                          :timestamp        :int
                          :contacts         {:type       :list
                                             :objectType :chat-contact}
                          :removed-at       {:type     :int
                                             :optional true}
                          :removed-from-at  {:type     :int
                                             :optional true}
                          :added-to-at      {:type     :int
                                             :optional true}
                          :updated-at       {:type     :int
                                             :optional true}
                          :last-message-id  :string
                          :message-overhead {:type    :int
                                             :default 0}
                          :public-key       {:type     :string
                                             :optional true}
                          :private-key      {:type     :string
                                             :optional true}
                          :pending-contact? {:type    :bool
                                             :default false}
                          :contact-info     {:type     :string
                                             :optional true}}})

(defn migration [_ _]
  (log/debug "migrating chat schema"))
