(ns schema.common)

(def ^:private ?timestamp
  [:or zero? pos-int?])

(def ^:private ?public-key
  [:string {:min 1}])

(def ^:private ?style
  [:map-of :keyword [:or :int :string :keyword]])

(def ^:private ?theme
  [:enum :light :dark])

(def ^:private ?icon-name
  [:qualified-keyword {:namespace :i}])

(def ^:private ?translation
  [:qualified-keyword {:namespace :t}])

(def ^:private ?color
  [:or
   :string
   [:enum
    :army
    :beige
    :blue
    :brown
    :camel
    :copper
    :danger
    :green
    :indigo
    :magenta
    :orange
    :pink
    :primary
    :purple
    :red
    :sky
    :success
    :turquoise
    :yellow]])

(def schemas
  {::color       ?color
   ::icon-name   ?icon-name
   ::public-key  ?public-key
   ::style       ?style
   ::theme       ?theme
   ::timestamp   ?timestamp
   ::translation ?translation})
