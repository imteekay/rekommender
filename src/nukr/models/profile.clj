(ns nukr.models.profile)

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
