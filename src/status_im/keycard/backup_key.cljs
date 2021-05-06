(ns status-im.keycard.backup-key
  (:require [status-im.utils.fx :as fx]
            [re-frame.core :as re-frame]
            [status-im.utils.utils :as utils]
            [status-im.i18n.i18n :as i18n]
            [status-im.ethereum.mnemonic :as mnemonic]
            [status-im.multiaccounts.model :as multiaccounts.model]
            [status-im.multiaccounts.recover.core :as multiaccounts.recover]
            [status-im.navigation :as navigation]
            [taoensso.timbre :as log]))

(fx/defn recovery-card-pressed
  {:events [:keycard-settings.ui/recovery-card-pressed]}
  [{:keys [db] :as cofx} checked?]
  (fx/merge cofx
            {:db (assoc-in db [:keycard :factory-reset-card?] true)}
            (utils/show-confirmation {:title               (i18n/label :t/keycard-recover-title)
                                      :content             (i18n/label :t/keycard-recover-text)
                                      :confirm-button-text (i18n/label :t/yes)
                                      :cancel-button-text  (i18n/label :t/no)
                                      :on-accept           #(re-frame/dispatch [:keycard-settings.ui/backup-card-pressed])
                                      :on-cancel           #()})))

(fx/defn backup-card-pressed
  {:events [:keycard-settings.ui/backup-card-pressed]}
  [{:keys [db] :as cofx}]
  (log/debug "[keycard] start backup")
  (fx/merge cofx
            {:db (-> db
                     (assoc-in [:keycard :creating-backup?] true))}
            (if (multiaccounts.model/logged-in? cofx)
              (navigation/navigate-to-cofx :seed-phrase nil)
              (navigation/navigate-to-cofx :key-storage-stack {:screen :seed-phrase}))))

(fx/defn start-keycard-backup
  {:events [::start-keycard-backup]}
  [{:keys [db] :as cofx}]
  {::multiaccounts.recover/import-multiaccount {:passphrase (-> db
                                                                :multiaccounts/key-storage
                                                                :seed-phrase
                                                                mnemonic/sanitize-passphrase)
                                                :password nil
                                                :success-event ::create-backup-card}})
(fx/defn create-backup-card
  {:events [::create-backup-card]}
  [{:keys [db] :as cofx} root-data derived-data]
  (fx/merge cofx
            {:db  (-> db
                      (update :intro-wizard
                              assoc
                              :root-key root-data
                              :derived derived-data
                              :recovering? true
                              :selected-storage-type :advanced)
                      (assoc-in [:keycard :flow] :recovery)
                      (update :multiaccounts/key-storage dissoc :seed-phrase))
             :dismiss-keyboard nil}
            (if (multiaccounts.model/logged-in? cofx)
              (navigation/navigate-to-cofx :keycard-onboarding-intro nil)
              (navigation/navigate-to-cofx :intro-stack {:screen :keycard-onboarding-intro}))))