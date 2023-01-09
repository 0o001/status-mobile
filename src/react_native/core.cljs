(ns react-native.core
  (:require ["@react-native-community/blur" :as blur]
            ["react" :as react]
            ["react-native" :as react-native]
            [cljs-bean.core :as bean]
            [oops.core :as oops]
            [react-native.flat-list :as flat-list]
            [react-native.platform :as platform]
            [react-native.section-list :as section-list]
            [reagent.core :as reagent]))

(def app-state ^js (.-AppState ^js react-native))
(def blur-view (reagent/adapt-react-class (.-BlurView blur)))

(def view (reagent/adapt-react-class (.-View ^js react-native)))
(def scroll-view (reagent/adapt-react-class (.-ScrollView ^js react-native)))
(def image (reagent/adapt-react-class (.-Image ^js react-native)))
(def text (reagent/adapt-react-class (.-Text ^js react-native)))
(def text-input (reagent/adapt-react-class (.-TextInput ^js react-native)))
(def safe-area-view (reagent/adapt-react-class (.-SafeAreaView ^js react-native)))

(def touchable-opacity (reagent/adapt-react-class (.-TouchableOpacity ^js react-native)))
(def touchable-highlight (reagent/adapt-react-class (.-TouchableHighlight ^js react-native)))
(def touchable-without-feedback
  (reagent/adapt-react-class (.-TouchableWithoutFeedback ^js react-native)))

(def flat-list flat-list/flat-list)

(def section-list section-list/section-list)

(def activity-indicator (reagent/adapt-react-class (.-ActivityIndicator ^js react-native)))

(def modal (reagent/adapt-react-class (.-Modal ^js react-native)))

(def keyboard ^js (.-Keyboard ^js react-native))

(def dismiss-keyboard! #(.dismiss keyboard))

(defn use-window-dimensions
  []
  (let [window ^js (react-native/useWindowDimensions)]
    {:font-scale (.-fontScale window)
     :height     (.-height window)
     :scale      (.-scale window)
     :width      (.-width window)}))

(defn hide-splash-screen
  []
  (.hide ^js (-> react-native .-NativeModules .-SplashScreen)))

(defn alert
  [title message buttons options]
  (.alert (.-Alert ^js react-native) title message (clj->js buttons) (clj->js options)))

(def appearance ^js (.-Appearance ^js react-native))

(defn get-color-scheme
  []
  (.getColorScheme appearance))

(defn appearance-add-change-listener
  [handler]
  (.addChangeListener appearance handler))

(defn get-window
  []
  (js->clj (.get (.-Dimensions ^js react-native) "window") :keywordize-keys true))

(def status-bar (.-StatusBar ^js react-native))

(defn set-status-bar-style
  [style]
  (.setBarStyle (.-StatusBar ^js react-native) style))

(def style-sheet (.-StyleSheet ^js react-native))

(defn status-bar-height
  []
  (.-currentHeight ^js status-bar))

(defn hw-back-add-listener
  [callback]
  (.addEventListener (.-BackHandler ^js react-native) "hardwareBackPress" callback))

(defn hw-back-remove-listener
  [callback]
  (.removeEventListener (.-BackHandler ^js react-native) "hardwareBackPress" callback))

(def keyboard-avoiding-view-class (reagent/adapt-react-class (.-KeyboardAvoidingView react-native)))

(defn keyboard-avoiding-view
  [props & children]
  (into [keyboard-avoiding-view-class
         (merge (when platform/ios? {:behavior :padding})
                props)]
        children))

(defn use-effect
  ([effect] (use-effect effect []))
  ([effect deps]
   (react/useEffect effect (bean/->js deps))))

(def use-ref react/useRef)
(defn use-effect-once [effect] (use-effect effect))
(defn use-unmount
  [f]
  (let [fn-ref (use-ref f)]
    (oops/oset! fn-ref "current" f)
    (use-effect-once (fn [] (fn [] (oops/ocall! fn-ref "current"))))))
