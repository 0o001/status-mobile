(ns status-im2.contexts.shell.schema
  (:require [status-im2.constants :as constants]
            [status-im2.contexts.shell.activity-center.notification-types :as notification-types]))

(def ^:private ?contact-request-state
  [:enum
   constants/contact-request-message-state-none
   constants/contact-request-message-state-pending
   constants/contact-request-message-state-accepted
   constants/contact-request-message-state-declined])

(def ^:private ?message
  [:map {:closed true}
   [:command-parameters [:maybe :any]]
   [:contact-request-state [:maybe ?contact-request-state]]])

(def ^:private ?notification-type
  [:enum
   notification-types/no-type
   notification-types/one-to-one-chat
   notification-types/private-group-chat
   notification-types/mention
   notification-types/reply
   notification-types/contact-request
   notification-types/admin
   notification-types/contact-verification])

(def ^:private ?notification
  [:map {:closed true}
   [:name [:string {:min 1}]]
   [:accepted :boolean]
   [:read :boolean]
   [:timestamp :s/unix-timestamp]
   [:dismissed :boolean]
   [:id :s/public-key]
   [:author :s/public-key]
   [:chat-id :s/public-key]
   [:last-message [:maybe ?message]]
   [:message ?message]
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
   [:type
    ?notification-type]])

(defn schemas
  []
  {:s/notification      ?notification
   :s/notification.type ?notification-type})
