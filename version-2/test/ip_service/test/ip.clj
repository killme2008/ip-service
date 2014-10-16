(ns ip-service.test.ip
  (:require [clojure.test :refer :all]
            [ip-service.ip :refer :all]
            [clojure.data.json :as json]
            [ring.mock.request :as mock]))

(deftest test-find-geography
  (testing "find-geography"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo #"llegal address,only supports IPv4."
         (find-geography nil)))
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo #"llegal address,only supports IPv4."
         (find-geography "localhost")))
    (is (= "中国\t江苏\t苏州\t" (find-geography "180.117.51.245")))
    (is (= "中国\t上海\t上海\t" (find-geography "116.227.225.210")))))
