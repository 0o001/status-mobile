(ns status-im.ui2.screens.chat.components.contact-item.view
  (:require [status-im.utils.re-frame :as rf]
            [quo2.foundations.typography :as typography]
            [quo2.components.icon :as icons]
            [quo2.foundations.colors :as colors]
            [quo2.components.avatars.user-avatar :as user-avatar]
            [quo.react-native :as rn]
            [status-im.utils.utils :refer [get-shortened-address]]
            [quo.platform :as platform]
            [quo2.components.markdown.text :as text]
            [status-im.ui2.screens.chat.components.contact-bottom-sheet.view :refer [contact-bottom-sheet]]
            [status-im.ui2.screens.chat.components.message-home-item.style :as style]))

(defn open-chat [chat-id]
  (rf/dispatch [:dismiss-keyboard])
  (if platform/android?
    (rf/dispatch [:chat.ui/navigate-to-chat-nav2 chat-id])
    (rf/dispatch [:chat.ui/navigate-to-chat chat-id]))
  (rf/dispatch [:search/home-filter-changed nil])
  (rf/dispatch [:accept-all-activity-center-notifications-from-chat chat-id]))

(defn contact-item [item]
  (let [{:keys [public-key ens-verified added? images]} item
        display-name (first (rf/sub [:contacts/contact-two-names-by-identity public-key]))
        photo-path   (if-not (empty? images) (rf/sub [:chats/photo-path public-key]) nil)]
    [rn/touchable-opacity (merge {:style         (style/container)
                                  :on-press      #(open-chat public-key)
                                  :on-long-press #(rf/dispatch [:bottom-sheet/show-sheet
                                                                {:content (fn [] [contact-bottom-sheet item])}])})
     [user-avatar/user-avatar {:full-name         display-name
                               :profile-picture   photo-path
                               :status-indicator? true
                               :online?           true
                               :size              :small
                               :ring?             false}]
     [rn/view {:style {:margin-left 8}}
      [rn/view {:style {:flex-direction :row}}
       [text/text {:style (merge typography/paragraph-1 typography/font-semi-bold
                                 {:color (colors/theme-colors colors/neutral-100 colors/white)})}
        display-name]
       (if ens-verified
         [rn/view {:style {:margin-left 5 :margin-top 4}}
          [icons/icon :main-icons2/verified {:no-color true :size 12 :color (colors/theme-colors colors/success-50 colors/success-60)}]]
         (when added?
           [rn/view {:style {:margin-left 5 :margin-top 4}}
            [icons/icon :main-icons2/contact {:no-color true :size 12 :color (colors/theme-colors colors/primary-50 colors/primary-60)}]]))]
      [text/text {:size  :paragraph-1
                  :style {:color (colors/theme-colors colors/neutral-50 colors/neutral-40)}}
       (get-shortened-address public-key)]]
     [rn/touchable-opacity {:style          {:position :absolute
                                             :right    20}
                            :active-opacity 1
                            :on-press       #(rf/dispatch [:bottom-sheet/show-sheet
                                                           {:content (fn [] [contact-bottom-sheet item])}])}
      [icons/icon :main-icons2/options {:size 20 :color (colors/theme-colors colors/primary-50 colors/primary-60)}]]]))