(ns nukr.models.connection)

(defn make-connection
  [first-profile-id second-profile-id]
  {:id                (str (gensym "c"))
   :first-profile-id  first-profile-id
   :second-profile-id second-profile-id})

(defn get-connections
  [database]
  (:connections database))
