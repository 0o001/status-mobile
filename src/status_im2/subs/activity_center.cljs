(ns status-im2.subs.activity-center
  (:require [re-frame.core :as re-frame]
            [status-im2.constants :as constants]
            [status-im2.contexts.shell.activity-center.notification-types :as types]
            malli.generator
            malli.dev.pretty
            [malli.core :as malli]))

(re-frame/reg-sub
 :activity-center/notifications
 :<- [:activity-center]
 (fn [activity-center]
   (:notifications activity-center)))

(re-frame/reg-sub
 :activity-center/unread-counts-by-type
 :<- [:activity-center]
 (fn [activity-center]
   (:unread-counts-by-type activity-center)))

(re-frame/reg-sub
 :activity-center/notification-types-with-unread
 :<- [:activity-center/unread-counts-by-type]
 (fn [unread-counts]
   (reduce-kv
    (fn [acc notification-type unread-count]
      (if (pos? unread-count)
        (conj acc notification-type)
        acc))
    #{}
    unread-counts)))

(re-frame/reg-sub
 :activity-center/unread-count
 :<- [:activity-center/unread-counts-by-type]
 (fn [unread-counts]
   (->> unread-counts
        vals
        (reduce + 0))))

(re-frame/reg-sub
 :activity-center/seen?
 :<- [:activity-center]
 (fn [activity-center]
   (:seen? activity-center)))

(re-frame/reg-sub
 :activity-center/unread-indicator
 :<- [:activity-center/seen?]
 :<- [:activity-center/unread-count]
 (fn [[seen? unread-count]]
   (cond
     (zero? unread-count) :unread-indicator/none
     seen?                :unread-indicator/seen
     :else                :unread-indicator/new)))

(re-frame/reg-sub
 :activity-center/mark-all-as-read-undoable-till
 :<- [:activity-center]
 (fn [activity-center]
   (:mark-all-as-read-undoable-till activity-center)))

(re-frame/reg-sub
 :activity-center/filter-status
 :<- [:activity-center]
 (fn [activity-center]
   (get-in activity-center [:filter :status])))

(re-frame/reg-sub
 :activity-center/filter-type
 :<- [:activity-center]
 (fn [activity-center]
   (get-in activity-center [:filter :type] types/no-type)))

(re-frame/reg-sub
 :activity-center/filter-status-unread-enabled?
 :<- [:activity-center/filter-status]
 (fn [filter-status]
   (= :unread filter-status)))

(re-frame/reg-sub
 :activity-center/pending-contact-requests
 :<- [:activity-center]
 (fn [activity-center]
   (:contact-requests activity-center)))

(comment
  (def test-instrumentation
    (malli/-instrument
     {:schema [:=> [:cat :s/notification] :s/notification]
      :report (malli.dev.pretty/reporter)}
     (fn [notification]
       (:id notification))))

  (test-instrumentation (malli.generator/generate :s/notification)))

(def ?activity-center-notifications
  [:map-of
   :s/notification.type
   [:map
    [:all {:optional true}
     [:map
      [:cursor :string]
      [:data [:sequential :s/notification]]]]]])

(comment
  @(re-frame/subscribe [:activity-center/notifications]))

(comment
  (re-frame/reg-sub
   :activity-center/pending-contact-requests
   :<- [:activity-center/notifications]
   (malli/-instrument
    {:schema [:=> [:cat ?activity-center-notifications :any]
              [:sequential :s/notification]]
     :report (malli.dev.pretty/reporter)}
    (fn [notifications]
      (filter (fn [{:keys [message]}]
                (= constants/contact-request-message-state-pending
                   (:contact-request-state message)))
              (get-in notifications [types/contact-request :unread :data]))))))
