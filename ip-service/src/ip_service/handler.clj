(ns ip-service.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [hbs.core :as hbs]
            [ip-service.ip :as ip-service]
            [clojure.data.json :as json]
            [compojure.route :as route]))

(defn json-response [body & {:keys [status headers] :or {status 200}}]
  (merge
   {:status status
    :headers
    (merge
     {"Content-Type" "application/json;charset=utf-8"}
     headers)
    :body (json/write-str body)}))

(defn query-api
  "A handler to query IP geography."
  [req]
  (let [ip (-> req :params :ip)]
    (json-response
     {:ip ip
      :result (ip-service/find-geography ip)})))

(defn query-page [req]
  (let [ip (or (-> req :params :ip))]
    (hbs/render-file "index"
                     {:ip ip
                      :result (when ip
                                (ip-service/find-geography ip))})))

;;middlewares
(defn json-req? [{content-type :content-type}]
  (and content-type
       (re-find #"(?i)application/json" content-type)))

(defn- wrap-error-handler [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable e
        (if (json-req? req)
          (json-response {:error (.getMessage e)} :status 400)
          {:status 400
           :body (hbs/render-file "index" {:error (.getMessage e)})})))))

(defroutes app-routes
  (GET "/" [] query-page)
  (context "/1" []
           (GET "/ip" [] query-api))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site
   (->
    app-routes
    wrap-error-handler)))
