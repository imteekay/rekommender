(ns nukr.interceptors.database)

(defonce database (atom {:profiles [] :connections []}))

(defn db-enter
  [context]
  (update context :request assoc :database @database))

(defn db-leave
  [context]
  (if-let [[operation & params] (:tx-data context)]
    (do
      (apply swap! database operation params)
      (assoc-in context [:request :database] @database))
    context))

(def db-interceptor
  {:name :database-interceptor
   :enter db-enter
   :leave db-leave})
