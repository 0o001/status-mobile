(ns utils.image-server-test
  (:require [cljs.test :refer [deftest is]]
            [utils.image-server :as sut]))

(deftest get-account-image-uri
  (with-redefs [sut/timestamp (constantly 99)]
    (is
     (=
      "https://localhost:port/accountImages?publicKey=public-key&keyUid=key-uid&imageName=image-name&theme=2&clock=99&addRing=1"
      (sut/get-account-image-uri {:port       "port"
                                  :public-key "public-key"
                                  :image-name "image-name"
                                  :key-uid    "key-uid"
                                  :theme      :dark
                                  :ring?      true})))))
