(ns nukr.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]))

(defonce database (atom {}))

(defn response [status body & {:as headers}]
  {:status status :body body :headers headers})

(def ok       (partial response 200))
(def created  (partial response 201))
(def accepted (partial response 202))

(defn add-profile
  [new-profile]
  (if-let [profiles (get @database :profiles)]
    (conj profiles new-profile)
    [new-profile]))

(def db-interceptor
  {:name :database-interceptor
   :enter (fn [context]
            (update context :request assoc :database @database))
   :leave (fn [context]
            (let [args   (:tx-data context)
                  table  (first (map key args))
                  params (get args table)]
              (if (and args table params)
                (do
                  (swap! database assoc table (add-profile params))
                  (assoc-in context [:request :database] @database))
                context)))})

(defn make-profile
  [id username]
  {:id       id
   :username username
   :opt-in   true})

(def profile-create
  {:name :profile-create
   :enter (fn [context]
            (let [username    (get-in context [:request :json-params :username] "Unnamed Profile")
                  db-id       (str (gensym "l"))
                  new-profile (make-profile db-id username)]
              (assoc context
                     :response (created new-profile)
                     :tx-data {:profiles new-profile})))})

(def profiles-list
  {:name :profiles-list
   :enter (fn [context]
            (if-let [profiles (get-in context [:request :database :profiles])]
              (assoc context :response (ok profiles))
              (assoc context :response (ok []))))})

(def routes
  (route/expand-routes
   #{["/api/profiles" :post [db-interceptor (body-params/body-params) http/json-body profile-create]]
     ["/api/profiles" :get [db-interceptor http/json-body profiles-list]]}))

(def service {:env :prod
              ::http/routes routes
              ::http/resource-path "/public"
              ::http/type :jetty
              ::http/port 8080
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})
