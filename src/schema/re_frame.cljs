(ns schema.re-frame
  (:require malli.util))

(def ^:private ?activity-center
  [:map {:closed true}
   [:filter {:required true}
    [:map
     [:status [:enum :read :unread]]
     [:type :int]]]
   [:loading? {:optional true} :boolean]
   [:contact-requests {:optional true} :any]
   [:cursor {:optional true} :string]
   [:notifications {:optional true} [:sequential :schema.shell/notification]]
   [:seen? {:optional true} :boolean]
   [:unread-counts-by-type {:optional true}
    [:map-of {:min 1} :schema.shell/notification-type :int]]])

(def ^:private ?event
  [:catn
   [:event-id :keyword]
   [:event-args [:* :any]]])

(def ^:private ?db
  [:map [:activity-center {:optional true} ?activity-center]])

(def ^:private ?effects
  [:map
   [:db {:optional true} ?db]
   [:json-rpc/call {:optional true} :schema.fx/rpc-call]])

(def ^:private ?cofx
  [:map
   [:db ?db]])

(def schemas
  {::cofx    ?cofx
   ::db      ?db
   ::effects ?effects
   ::event   ?event})
