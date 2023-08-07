(ns schema.fx)

(def ^:private ?rpc-call
  [:vector
   [:map {:closed true}
    [:method [:re #"^wakuext_.+$"]]
    [:params [:vector :any]]
    [:on-success [:or :schema.re-frame/event fn?]]
    [:on-error [:or :schema.re-frame/event fn?]]]])

(def schemas
  {::rpc-call ?rpc-call})
