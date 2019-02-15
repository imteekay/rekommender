(ns nukr.interceptors.profile
  (:require [nukr.http-helpers :refer :all]))

(defn make-profile
  [id username]
  {:id       id
   :username username
   :opt-in   true})

(defn find-profile-by-id
  [database profile-id]
  (->> (:profiles database)
       (filter #(= profile-id (:id %)))
       first))

(def entity-render
  {:name :entity-render
   :leave (fn [context]
            (if-let [profile (:result context)]
              (assoc context :response (ok profile))))})

(def profile-view
  {:name :profile-view
   :leave (fn [context]
            (if-let [profile-id (get-in context [:request :path-params :id])]
              (if-let [profile (find-profile-by-id (get-in context [:request :database]) profile-id)]
                (assoc context :result profile)
                context)
              context))})

(defn add-profile
  [database new-profile]
  (if-let [profiles (:profiles database)]
    (assoc database :profiles (conj profiles new-profile))
    (assoc database :profiles [new-profile])))

(def profile-create
  {:name :profile-create
   :enter (fn [context]
            (let [username    (get-in context [:request :json-params :username] "Unnamed Profile")
                  db-id       (str (gensym "l"))
                  new-profile (make-profile db-id username)]
              (assoc context
                     :response (created new-profile)
                     :tx-data [add-profile new-profile])))})

(def profiles-list
  {:name :profiles-list
   :enter (fn [context]
            (if-let [profiles (get-in context [:request :database :profiles])]
              (assoc context :response (ok profiles))
              (assoc context :response (ok []))))})

(defn update-opt-in
  [profile-id opt-in profiles]
  (map (fn [profile]
         (if (= profile-id (:id profile))
           (assoc profile :opt-in opt-in)
           profile))
       profiles))

(defn update-profile
  [database profile-id opt-in]
  (if-let [profile (find-profile-by-id database profile-id)]
    (update-in database [:profiles] (partial update-opt-in profile-id opt-in))
    database))

(def profile-update
  {:name :profile-update
   :enter (fn [context]
            (if-let [profile-id (get-in context [:request :path-params :id])]
              (let [opt-in (get-in context [:request :json-params :opt-in])]
                (assoc context :tx-data [update-profile profile-id opt-in]))))})
