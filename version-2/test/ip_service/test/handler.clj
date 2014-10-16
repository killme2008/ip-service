(ns ip-service.test.handler
  (:require [clojure.test :refer :all]
            [ip-service.handler :refer :all]
            [clojure.data.json :as json]
            [ring.mock.request :as mock]))

(defn json-request
  "Mock json request."
  [method uri & params]
  (-> (apply mock/request method uri params)
      (update-in  [:headers]
             #(merge % {"Content-Type" "application/json"}))
      (assoc :content-type "application/json")))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (re-find #"<h2>IP Service</h2>" (:body response))))
    (let [response (app (mock/request :get "/?ip=127.0.0.1"))]
      (is (= (:status response) 200))
      (is (re-find #"127.0.0.1  &nbsp;&nbsp;&nbsp;   本机地址	本机地址" (:body response))))
    (let [response (app (mock/request :get "/?ip=180.117.51.245"))]
      (is (= (:status response) 200))
      (is (re-find #"180.117.51.245  &nbsp;&nbsp;&nbsp;   中国	江苏	苏州	" (:body response))))
    (let [response (app (mock/request :get "/?ip=localhost"))]
      (is (= (:status response) 400))
      (is (re-find #"Illegal address,only supports IPv4." (:body response)))))

  (testing "REST API"
    (let [response (app (json-request :get "/1/ip?ip=180.117.51.245"))]
      (is (= (:status response) 200))
      (is (= "application/json;charset=utf-8" (get-in response [:headers "Content-Type"])))
      (is (= {:ip "180.117.51.245" :result "中国\t江苏\t苏州\t"} (json/read-str (:body response) :key-fn keyword))))
    (let [response (app (json-request :get "/1/ip?ip=0:0:0:0:0:0"))]
      (is (= (:status response) 400))
      (is (= "application/json;charset=utf-8" (get-in response [:headers "Content-Type"])))
      (is (= {:error "Illegal address,only supports IPv4."} (json/read-str (:body response) :key-fn keyword)))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
