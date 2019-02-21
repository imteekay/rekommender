(ns rekommender.interceptors.connection
  (:require [rekommender.http-helpers :refer :all]
            [rekommender.models.connection :as connection-model]))

(defn add-connection
  [database connection]
  (if-let [connections (:connections database)]
    (assoc database :connections (conj connections connection))))

(defn connection-create-enter
  [context]
  (if-let [first-profile-id (get-in context [:request :json-params :first-profile-id])]
    (if-let [second-profile-id (get-in context [:request :json-params :second-profile-id])]
      (let [new-connection (connection-model/make-connection first-profile-id second-profile-id)]
        (assoc context
               :response (created new-connection)
               :data [add-connection new-connection])))))

(def connection-create
  {:name :connection-create
   :enter connection-create-enter})
