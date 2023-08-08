(ns schema.shell
  (:require [schema.registry :as registry]
            [status-im2.constants :as constants]
            [status-im2.contexts.shell.activity-center.notification-types :as notification-types]))

(defn- ?contact-request-state
  []
  [:enum
   constants/contact-request-message-state-none
   constants/contact-request-message-state-pending
   constants/contact-request-message-state-accepted
   constants/contact-request-message-state-declined])

(defn- ?message
  []
  [:map {:closed true}
   [:alias :string]
   [:chat-id :string]
   [:clock-value :schema.common/timestamp]
   [:command-parameters :any]
   [:compressed-key :string]
   [:contact-request-state [:maybe ::contact-request-state]]
   [:content :any]
   [:content-type :int]
   [:display-name :string]
   [:emojiHash [:sequential :string]]
   [:from :string]
   [:identicon :string]
   [:link-previews [:sequential :any]]
   [:message-id :string]
   [:message-type :int]
   [:new? {:optional true} :boolean]
   [:outgoing :boolean]
   [:outgoing-status :any]
   [:quoted-message :any]
   [:replace :string]
   [:seen :boolean]
   [:timestamp :schema.common/timestamp]
   [:whisper-timestamp :schema.common/timestamp]])

(defn- ?notification-type
  []
  [:enum
   notification-types/no-type
   notification-types/one-to-one-chat
   notification-types/private-group-chat
   notification-types/mention
   notification-types/reply
   notification-types/contact-request
   notification-types/community-request
   notification-types/admin
   notification-types/community-kicked
   notification-types/contact-verification])

(defn- ?notification
  []
  [:map {:closed true}
   [:name [:string {:min 1}]]
   [:deleted :boolean]
   [:membership-status :any]
   [:community-id :any]
   [:accepted :boolean]
   [:read :boolean]
   [:timestamp :schema.common/timestamp]
   [:dismissed :boolean]
   [:id :schema.common/public-key]
   [:author :schema.common/public-key]
   [:chat-id :schema.common/public-key]
   [:last-message [:maybe ::message]]
   [:updatedAt :schema.common/timestamp]
   [:message ::message]
   [:reply-message [:maybe :any]]
   [:contact-verification-status
    [:maybe
     [:enum
      constants/contact-verification-status-unknown
      constants/contact-verification-status-pending
      constants/contact-verification-status-accepted
      constants/contact-verification-status-declined
      constants/contact-verification-status-cancelled
      constants/contact-verification-status-trusted
      constants/contact-verification-status-untrustworthy]]]
   [:type ::notification-type]])

(defn register-schemas
  []
  (registry/def ::contact-request-state (?contact-request-state))
  (registry/def ::message (?message))
  (registry/def ::notification-type (?notification-type))
  (registry/def ::notification (?notification)))
