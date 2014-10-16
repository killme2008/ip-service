(ns ip-service.server
  (:gen-class)
  (:use [ring.adapter.jetty]
        [ip-service.handler :only [app]])
  (:require [clojure.tools.cli :refer [parse-opts]]))

(def service-options
  [["-p" "--port PORT" "Port number"
    :default 3000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-t" "--threads THREADS" "Jetty thread number"
    :default 50
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 %) "Must be a number greater than 0"]]
   ;; A boolean option defaulting to nil
   ["-h" "--help"]])

(defn- start-server [{:keys [port threads]}]
  (println "Start IP service at port:" port)
  (run-jetty app
             {:port port
              :max-threads threads
              :join? false }))

(defn -main [& args]
  (let [{:keys [summary options] :as opts} (parse-opts args service-options)]
    (if (:help options)
      (println (:summary opts))
      (start-server options))))
