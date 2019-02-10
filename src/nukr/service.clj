(ns nukr.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]))

(defn get-profiles
  [request]
  (ring-resp/response {:profiles [{:id 1 :username "TK" :opt-in true}
                                  {:id 2 :username "Kazu" :opt-in true}
                                  {:id 3 :username "Kaio" :opt-in true}]}))

(def common-interceptors [(body-params/body-params) http/html-body])

(def routes #{["/api/profiles" :get (conj common-interceptors `get-profiles)]})

(def service {:env :prod
              ::http/routes routes
              ::http/resource-path "/public"
              ::http/type :jetty
              ::http/port 8080
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})

