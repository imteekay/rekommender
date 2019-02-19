(ns nukr.models.connection-test
  (:require [clojure.test :refer :all]
            [nukr.models.connection :refer :all]))

(deftest get-connections-test
  (testing "Gets database connections from an empty database"
    (let [database (atom {:profiles [] :connections []})]
      (is (= [] (get-connections @database)))))

  (testing "Gets database connections"
    (let [connection {:id 1 :first-profile-id 1 :second-profile-id 2}
          database (atom {:profiles [] :connections [connection]})]
      (is (= [connection] (get-connections @database))))))
