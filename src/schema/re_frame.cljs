(ns schema.re-frame
  (:require [schema.registry :as registry]))

(defn ?event
  []
  [:catn
   [:event-id :keyword]
   [:event-args [:* :any]]])

(defn ?cofx
  []
  [:map
   [:db ::db]])

;;;; Effects

(defn- ?rpc-call
  []
  [:vector
   [:map {:closed true}
    [:method [:re #"^wakuext_.+$"]]
    [:params [:vector :any]]
    [:on-success [:or ::event fn?]]
    [:on-error [:or ::event fn?]]]])

(defn ?effects
  []
  [:map
   [:db {:optional true} ::db]
   [:json-rpc/call {:optional true} ::rpc-call]])

;;;; App DB

(defn ?activity-center
  []
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

(defn ?db
  []
  [:map [:activity-center {:optional true} (?activity-center)]])

(defn register-schemas
  []
  (registry/def ::event (?event))
  (registry/def ::rpc-call (?rpc-call))
  (registry/def ::db (?db))
  (registry/def ::cofx (?cofx))
  (registry/def ::effects (?effects)))
