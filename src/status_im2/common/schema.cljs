(ns status-im2.common.schema)

(def ^:private ?unix-timestamp
  [:or zero? pos-int?])

(def ^:private ?public-key
  [:string {:min 1}])

(def ^:private ?style
  [:map-of :keyword [:or :int :string :keyword]])

(defn schemas
  []
  {:s/unix-timestamp ?unix-timestamp
   :s/style          ?style
   :s/public-key     ?public-key})
