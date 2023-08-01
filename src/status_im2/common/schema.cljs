(ns status-im2.common.schema)

(def ^:private ?unix-timestamp
  [:or zero? pos-int?])

(def ^:private ?public-key
  [:string {:min 1}])

(def ^:private ?style
  [:map-of :keyword [:or :int :string :keyword]])

(def ^:private ?theme
  [:enum :light :dark])

(defn schemas
  []
  {:s/public-key     ?public-key
   :s/style          ?style
   :s/theme          ?theme
   :s/unix-timestamp ?unix-timestamp})
