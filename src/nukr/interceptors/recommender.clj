(ns nukr.interceptors.recommender
  (:require [nukr.http-helpers :refer :all]
            [nukr.interceptors.profile :as profile-interceptor]
            [nukr.models.profile :as profile-model]
            [nukr.models.connection :as connection-model]
            [nukr.models.profile.recommendation :as recommendation]))

(defn get-profiles-suggestion
  [database profile-id]
  (if-let [profile (profile-model/find-profile-by-id database profile-id)]
    (let [profiles (profile-model/get-profiles database)
          connections (connection-model/get-connections database)]
      (recommendation/profiles-suggestion profile profiles connections))))

(defn profiles-suggestion-enter
  [context]
  (if-let [profile-id (get-in context [:request :path-params :id])]
    (if-let [database (get-in context [:request :database])]
      (let [suggestion (get-profiles-suggestion database profile-id)]
        (assoc context :response (ok suggestion))))))

(def profiles-suggestion
  {:name :profiles-suggestion
   :enter profiles-suggestion-enter})
