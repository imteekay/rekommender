(ns nukr.interceptors.database)

(defonce database (atom {}))

(def db-interceptor
  {:name :database-interceptor
   :enter (fn [context]
            (update context :request assoc :database @database))
   :leave (fn [context]
            (if-let [[operation & params] (:tx-data context)]
              (do
                (apply swap! database operation params)
                (assoc-in context [:request :database] @database))
              context))})
