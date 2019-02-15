(ns nukr.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [nukr.interceptors.profile :as profile-interceptors]))

(defonce database (atom {}))

(def db-interceptor
  {:name :database-interceptor
   :enter (fn [context]
            (update context :request assoc :database @database))
   :leave (fn [context]
            (if-let [[operation & params] (:tx-data context)]
              (do
                (apply swap! database operation params)
                (assoc-in context [:request :database] @database))
              context))})

(def routes
  (route/expand-routes
   #{["/api/profiles" :post [db-interceptor (body-params/body-params) http/json-body profile-interceptors/profile-create]]
     ["/api/profiles" :get [db-interceptor http/json-body profile-interceptors/profiles-list]]
     ["/api/profiles/:id" :put [(body-params/body-params) http/json-body profile-interceptors/entity-render profile-interceptors/profile-view db-interceptor profile-interceptors/profile-update]]}))

(def service {:env :prod
              ::http/routes routes
              ::http/resource-path "/public"
              ::http/type :jetty
              ::http/port 8080
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})
