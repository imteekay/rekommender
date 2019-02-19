(ns nukr.models.connection-test
  (:require [clojure.test :refer :all]
            [nukr.models.connection :refer :all]))

(deftest get-connections-test
  (let [database (atom {:profiles [] :connections []})]
    (testing "Gets database connections from an empty database"
      (is (= [] (get-connections @database)))))

  (let [connection {:id 1 :first-profile-id 1 :second-profile-id 2}
        database (atom {:profiles [] :connections [connection]})]
    (testing "Gets database connections"
      (is (= [connection] (get-connections @database))))))
