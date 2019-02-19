(ns nukr.models.profile.recommendation
  (:require [clojure.set :as set]))

(defn connected?
  [profile connection]
  (let [profile-id   (:id profile)
        profile-1-id (:first-profile-id connection)
        profile-2-id (:second-profile-id connection)]
    (or
     (= profile-1-id profile-id)
     (= profile-2-id profile-id))))

(defn get-profile-connection-id
  [profile connection]
  (let [profile-id   (:id profile)
        profile-1-id (:first-profile-id connection)
        profile-2-id (:second-profile-id connection)]
    (if (= profile-id profile-1-id)
      profile-2-id
      profile-1-id)))

(defn get-all-connections-ids
  "Gets all connections for a specific profile"
  [profile connections]
  (->> connections
       (filter #(connected? profile %))
       (map #(get-profile-connection-id profile %))))

(defn no-connection-between-profiles?
  "Verifies if there is no connection between two profiles"
  [profile connections other-profile]
  (->> (get-all-connections-ids profile connections)
       (some #(= % (:id other-profile)))
       not))

(defn not-the-same-profile?
  "Compares if the profiles are the same"
  [profile other-profile]
  (not= (:id profile) (:id other-profile)))

(defn count-connection-matches
  "Counts the number of connections that match between two profiles"
  [profile connections other-profile]
  (let [profile-1-connections (get-all-connections-ids profile connections)
        profile-2-connections (get-all-connections-ids other-profile connections)]
    (count
     (set/intersection (set profile-1-connections)
                       (set profile-2-connections)))))

(defn remove-the-same-profile
  [profile profiles]
  (filter #(not-the-same-profile? profile %) profiles))

(defn remove-connected-profiles
  [profile connections profiles]
  (filter #(no-connection-between-profiles? profile connections %) profiles))

(defn remove-opt-out-profiles
  [profiles]
  (filter #(:opt-in %) profiles))

(defn rank-profiles-with-more-connections-matches
  [profile connections profiles]
  (sort-by #(count-connection-matches profile connections %) > profiles))

(defn profiles-suggestion
  [profile profiles connections]
  (->> profiles
       (remove-the-same-profile profile)
       (remove-connected-profiles profile connections)
       remove-opt-out-profiles
       (rank-profiles-with-more-connections-matches profile connections)))
