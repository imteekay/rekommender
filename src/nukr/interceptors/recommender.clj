(ns nukr.interceptors.recommender
  (:require [nukr.http-helpers :refer :all]
            [nukr.interceptors.profile :as profile-interceptor]
            [nukr.models.profile :as profile-model]
            [nukr.models.connection :as connection-model]))

(defn connected?
  [profile connection]
  (let [profile-id   (:id profile)
        profile-1-id (:first-profile-id connection)
        profile-2-id (:second-profile-id connection)]
    (or
     (= profile-1-id profile-id)
     (= profile-2-id profile-id))))

(defn get-connected-profile
  [profile connection]
  (let [profile-id   (:id profile)
        profile-1-id (:first-profile-id connection)
        profile-2-id (:second-profile-id connection)]
    (if (= profile-id profile-1-id)
      profile-2-id
      profile-1-id)))

(defn get-all-connections
  "Gets all connections for a specific profile"
  [profile connections]
  (->> connections
       (filter #(connected? profile %))
       (map #(get-connected-profile profile %))))

(defn no-connection-between-profiles?
  "Verifies if there is no connection between two profiles"
  [profile connections other-profile]
  (->> (get-all-connections profile connections)
       (some #(= % (:id other-profile)))
       not))

(defn not-the-same-profile?
  "Compares if the profiles are the same"
  [profile other-profile]
  (not= (:id profile) (:id other-profile)))

(defn count-connection-matches
  "Counts the number of connections that match between two profiles"
  [profile connections other-profile]
  (let [profile-1-connections (get-all-connections profile connections)
        profile-2-connections (get-all-connections other-profile connections)]
    (count
     (clojure.set/intersection (set profile-1-connections)
                               (set profile-2-connections)))))

(defn remove-the-same-profile
  [profile profiles]
  (filter #(not-the-same-profile? profile %) profiles))

(defn remove-connected-profiles
  [profile connections profiles]
  (filter #(no-connection-between-profiles? profile connections %) profiles))

(defn remove-opt-out-profiles
  [profiles]
  (filter #(get % :opt-in) profiles))

(defn rank-profiles-with-more-connections-matches
  [profile connections profiles]
  (sort-by #(count-connection-matches profile connections %) > profiles))

(defn profiles-suggestions
  [profile profiles connections]
  (->> profiles
       (remove-the-same-profile profile)
       (remove-connected-profiles profile connections)
       remove-opt-out-profiles
       (rank-profiles-with-more-connections-matches profile connections)))

(defn get-profiles-suggestion
  [database profile-id]
  (if-let [profile (profile-model/find-profile-by-id database profile-id)]
    (let [profiles (profile-model/get-profiles database)
          connections (connection-model/get-connections database)]
      (profiles-suggestions profile profiles connections))))

(def profiles-suggestion
  {:name :profiles-suggestion
   :enter (fn [context]
            (if-let [profile-id (get-in context [:request :path-params :id])]
              (if-let [database (get-in context [:request :database])]
                (let [suggestion (get-profiles-suggestion database profile-id)]
                  (assoc context :response (ok suggestion))))))})
