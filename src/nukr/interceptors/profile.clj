(ns nukr.interceptors.profile
  (:require [nukr.http-helpers :refer :all]))

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
