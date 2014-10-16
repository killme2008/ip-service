(defproject ip-service "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojars.smee/binary "0.3.0"]
                 [ring/ring-jetty-adapter "1.2.1"]
                 [org.clojure/data.json "0.2.5"]
                 [org.clojure/tools.cli "0.3.1"]
                 [hbs "0.6.0"]
                 [compojure "1.1.9"]]
  :main ip-service.server
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler ip-service.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
