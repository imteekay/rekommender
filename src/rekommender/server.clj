(ns rekommender.server
  (:gen-class)
  (:require [io.pedestal.http :as server]
            [io.pedestal.http.route :as route]
            [rekommender.service :as service]))

(defonce runnable-service (server/create-server service/service))

(defn run-dev
  [& args]
  (-> service/service
      (merge {:env :dev
              ::server/join? false
              ::server/routes #(route/expand-routes (deref #'service/routes))
              ::server/allowed-origins {:creds true :allowed-origins (constantly true)}
              ::server/secure-headers {:content-security-policy-settings {:object-src "'none'"}}})
      server/default-interceptors
      server/dev-interceptors
      server/create-server
      server/start))

(defn -main
  [& args]
  (server/start runnable-service))
