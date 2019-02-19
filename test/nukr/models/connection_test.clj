(ns nukr.models.connection-test
  (:require [clojure.test :refer :all]
            [nukr.models.connection :refer :all]))

(deftest make-connection-test
  (testing "Makes a new profile"
    (let [first-profile-id 1
          second-profile-id 2
          connection (make-connection first-profile-id second-profile-id)]
      (is (= first-profile-id (:first-profile-id connection)))
      (is (= second-profile-id (:second-profile-id connection))))))

(deftest get-connections-test
  (testing "Gets database connections from an empty database"
    (let [database (atom {:profiles [] :connections []})]
      (is (= [] (get-connections @database)))))

  (testing "Gets database connections"
    (let [connection {:id 1 :first-profile-id 1 :second-profile-id 2}
          database (atom {:profiles [] :connections [connection]})]
      (is (= [connection] (get-connections @database))))))
