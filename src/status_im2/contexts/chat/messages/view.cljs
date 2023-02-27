(ns status-im2.contexts.chat.messages.view
  (:require [quo2.core :as quo]
            [re-frame.db]
            [react-native.core :as rn]
            [react-native.safe-area :as safe-area]
            [reagent.core :as reagent]
            [status-im2.contexts.chat.messages.composer.view :as composer]
            [status-im2.constants :as constants]
            [quo2.components.avatars.user-avatar :as user-avatar]
            [status-im2.contexts.chat.messages.list.view :as messages.list]
            [status-im2.contexts.chat.messages.contact-requests.bottom-drawer :as
             contact-requests.bottom-drawer]
            [status-im2.contexts.chat.messages.pin.banner.view :as pin.banner]
            [status-im2.navigation.state :as navigation.state]
            [status-im.ui.components.fast-image :as fast-image]
            [quo2.foundations.colors :as colors]
            [react-native.reanimated :as reanimated]
            [quo2.components.animated-header-flatlist.view :as animated-header-list]
            [utils.debounce :as debounce]
            [utils.re-frame :as rf]))

(defn navigate-back-handler
  []
  (when (and (not @navigation.state/curr-modal) (= (get @re-frame.db/app-db :view-id) :chat))
    (rn/hw-back-remove-listener navigate-back-handler)
    (rf/dispatch [:chat/close])
    (rf/dispatch [:navigate-back])
    ;; If true is not returned back button event will bubble up,
    ;; and will call system back button action
    true))

(defn page-nav
  []
  (let [{:keys [group-chat chat-id chat-name emoji
                chat-type]} (rf/sub [:chats/current-chat])
        display-name        (if (= chat-type constants/one-to-one-chat-type)
                              (first (rf/sub [:contacts/contact-two-names-by-identity chat-id]))
                              (str emoji " " chat-name))
        online?             (rf/sub [:visibility-status-updates/online? chat-id])
        contact             (when-not group-chat (rf/sub [:contacts/contact-by-address chat-id]))
        photo-path          (when-not (empty? (:images contact)) (rf/sub [:chats/photo-path chat-id]))]
    [quo/page-nav
     {:align-mid?            true

      :mid-section           (if group-chat
                               {:type      :text-only
                                :main-text display-name}
                               {:type      :user-avatar
                                :avatar    {:full-name       display-name
                                            :online?         online?
                                            :profile-picture photo-path
                                            :size            :medium}
                                :main-text display-name
                                :on-press  #(debounce/dispatch-and-chill [:chat.ui/show-profile chat-id]
                                                                         1000)})

      :left-section          {:on-press            #(do
                                                      (rf/dispatch [:chat/close])
                                                      (rf/dispatch [:navigate-back]))
                              :icon                :i/arrow-left
                              :accessibility-label :back-button}

      :right-section-buttons [{:on-press            #()
                               :style               {:border-width 1
                                                     :border-color :red}
                               :icon                :i/options
                               :accessibility-label :options-button}]}]))

(def theme-color (colors/theme-alpha "#5BCC95" 0.2 0.2))

(defn display-picture-comp
  [animation]
  (let [{:keys [group-chat chat-id chat-name emoji chat-type]} (rf/sub [:chats/current-chat])
        display-name        (if (= chat-type constants/one-to-one-chat-type)
                              (first (rf/sub [:contacts/contact-two-names-by-identity chat-id]))
                              (str emoji " " chat-name))
        online?             (rf/sub [:visibility-status-updates/online? chat-id])
        contact             (when-not group-chat (rf/sub [:contacts/contact-by-address chat-id]))
        photo-path          (when-not (empty? (:images contact)) (rf/sub [:chats/photo-path chat-id]))]
    [user-avatar/user-avatar {:full-name       display-name
                              :online?         online?
                              :profile-picture photo-path
                              :size            :big}]))

(defn header-comp
  []
  [rn/view
   {:style {:flex-direction  :row
            :justify-content :center
            :align-items     :center}}
   [fast-image/fast-image
    {:source {:uri
              "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Cat03.jpg/1200px-Cat03.jpg"}
     :style  {:width         32
              :height        32
              :border-radius 16
              :margin-right  8}}]
   [quo/text {:weight :semi-bold} "Alecia Keys"]])

(defn title-comp
  []
  [quo/text
   {:weight :semi-bold
    :size   :heading-1
    :style  {:margin-top  56
             :margin-left 20}} "Alicia Keys"])

(defn main-comp
  []
  (let [;;NOTE: we want to react only on these fields, do not use full chat map here
        {:keys [chat-id contact-request-state show-input?] :as chat}
        (rf/sub [:chats/current-chat-chat-view])]
    [messages.list/messages-list {:chat chat :show-input? show-input?}]))

(defn chat-render
  []
  (let [;;NOTE: we want to react only on these fields, do not use full chat map here
        {:keys [chat-id contact-request-state show-input? group-chat chat-id chat-name emoji chat-type chat-type] :as chat} (rf/sub [:chats/current-chat-chat-view])
        display-name        (if (= chat-type constants/one-to-one-chat-type)
                              (first (rf/sub [:contacts/contact-two-names-by-identity chat-id]))
                              (str emoji " " chat-name))
        online?             (rf/sub [:visibility-status-updates/online? chat-id])
        contact             (when-not group-chat (rf/sub [:contacts/contact-by-address chat-id]))
        photo-path          (when-not (empty? (:images contact)) (rf/sub [:chats/photo-path chat-id]))]
    [animated-header-list/animated-header-list
     {:theme-color          theme-color
      :cover-bg-color       "#2A799B33"
      :display-picture-comp (fn []
                              [user-avatar/user-avatar {:full-name       display-name
                                                        :online?         online?
                                                        :profile-picture photo-path
                                                        :size            :big}])
      :header-comp          (fn []
                              [rn/view
                               {:style {:flex-direction  :row
                                        :justify-content :center
                                        :align-items     :center}}
                               [user-avatar/user-avatar {:full-name       display-name
                                                         :online?         online?
                                                         :profile-picture photo-path
                                                         :size            :small}]
                               [quo/text {:weight :semi-bold} display-name]])
      :title-comp           (fn []
                              [quo/text
                               {:weight :semi-bold
                                :size   :heading-1
                                :style  {:margin-top  56
                                         :margin-left 20}} display-name])
      :main-comp            main-comp}]
    #_[safe-area/consumer
     (fn [insets]
       [rn/keyboard-avoiding-view
        {:style                  {:position :relative :flex 1}
         :keyboardVerticalOffset (- (:bottom insets))}
        [page-nav]
        [pin.banner/banner chat-id]
        [messages.list/messages-list {:chat chat :show-input? show-input?}]
        (if-not show-input?
          [contact-requests.bottom-drawer/view chat-id contact-request-state]
          [composer/composer chat-id insets])])]))

(defn chat
  []
  (reagent/create-class
   {:component-did-mount    (fn []
                              (rn/hw-back-remove-listener navigate-back-handler)
                              (rn/hw-back-add-listener navigate-back-handler))
    :component-will-unmount (fn [] (rn/hw-back-remove-listener navigate-back-handler))
    :reagent-render         chat-render}))
