(ns rekommender.models.profile)

(defn make-profile
  [username]
  {:id       (str (gensym "p"))
   :username username
   :opt-in   true})

(defn find-profile-by-id
  [database profile-id]
  (->> (:profiles database)
       (filter #(= profile-id (:id %)))
       first))

(defn get-profiles
  [database]
  (:profiles database))
