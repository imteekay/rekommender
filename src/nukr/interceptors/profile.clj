(ns nukr.interceptors.profile
  (:require [nukr.http-helpers :refer :all]
            [nukr.models.profile :as profile-model]))

;; Render Resource Interceptor

(def entity-render
  {:name :entity-render
   :leave (fn [context]
            (if-let [profile (:result context)]
              (assoc context :response (ok profile))))})

(def profile-view
  {:name :profile-view
   :leave (fn [context]
            (if-let [profile-id (get-in context [:request :path-params :id])]
              (if-let [profile (profile-model/find-profile-by-id (get-in context [:request :database]) profile-id)]
                (assoc context :result profile)
                context)
              context))})

;; Create Interceptor

(defn add-profile
  [database new-profile]
  (if-let [profiles (:profiles database)]
    (assoc database :profiles (conj profiles new-profile))))

(defn profile-create-enter
  [context]
  (let [username    (get-in context [:request :json-params :username] "Unnamed Profile")
        new-profile (profile-model/make-profile username)]
    (assoc context
           :response (created new-profile)
           :tx-data [add-profile new-profile])))

(def profile-create
  {:name :profile-create
   :enter profile-create-enter})

;; List Interceptor

(defn profiles-list-enter
  [context]
  (if-let [profiles (get-in context [:request :database :profiles])]
    (assoc context :response (ok profiles))
    (assoc context :response (ok []))))

(def profiles-list
  {:name :profiles-list
   :enter profiles-list-enter})

;; Update Interceptor

(defn update-opt-in
  [profile-id opt-in profiles]
  (map (fn [profile]
         (if (= profile-id (:id profile))
           (assoc profile :opt-in opt-in)
           profile))
       profiles))

(defn update-profile
  [database profile-id opt-in]
  (if-let [profile (profile-model/find-profile-by-id database profile-id)]
    (update-in database [:profiles] (partial update-opt-in profile-id opt-in))
    database))

(defn profile-update-enter
  [context]
  (if-let [profile-id (get-in context [:request :path-params :id])]
    (let [opt-in (get-in context [:request :json-params :opt-in])]
      (assoc context :tx-data [update-profile profile-id opt-in]))))

(def profile-update
  {:name :profile-update
   :enter profile-update-enter})
