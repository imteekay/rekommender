(ns rekommender.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [rekommender.interceptors.database :as database-interceptors]
            [rekommender.interceptors.profile :as profile-interceptors]
            [rekommender.interceptors.connection :as connection-interceptors]
            [rekommender.interceptors.recommender :as recommender-interceptors]))

(def routes
  (route/expand-routes
   #{["/api/profiles" :post [database-interceptors/db-interceptor (body-params/body-params) http/json-body profile-interceptors/profile-create]]
     ["/api/profiles" :get [database-interceptors/db-interceptor http/json-body profile-interceptors/profiles-list]]
     ["/api/profiles/:id" :put [(body-params/body-params) http/json-body profile-interceptors/entity-render profile-interceptors/profile-view database-interceptors/db-interceptor profile-interceptors/profile-update]]
     ["/api/profiles/:id/suggestions" :get [database-interceptors/db-interceptor http/json-body recommender-interceptors/profiles-suggestion]]
     ["/api/connections" :post [database-interceptors/db-interceptor (body-params/body-params) http/json-body connection-interceptors/connection-create]]}))

(def service {:env :prod
              ::http/routes routes
              ::http/resource-path "/public"
              ::http/type :jetty
              ::http/port 8080
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})
