(ns rekommender.models.profile-test
  (:require [clojure.test :refer :all]
            [rekommender.models.profile :refer :all]))

(deftest make-profile-test
  (testing "Makes a new profile"
    (let [username "TK"
          profile (make-profile username)]
      (is (= username (:username profile)))
      (is (= true (:opt-in profile))))))

(deftest find-profile-by-id-test
  (testing "Finds profile by a given id"
    (let [profile-id 1
          profile {:id profile-id :username "TK" :opt-in true}
          database (atom {:profiles [profile] :connections []})]
      (is (= profile (find-profile-by-id @database profile-id)))
      (is (= nil (find-profile-by-id @database (inc profile-id)))))))

(deftest get-profiles-test
  (testing "Gets database profiles from an empty database"
    (let [database (atom {:profiles [] :connections []})]
      (is (= [] (get-profiles @database)))))

  (testing "Gets database profiles"
    (let [profile {:id 1 :username "TK" :opt-in true}
          database (atom {:profiles [profile] :connections []})]
      (is (= [profile] (get-profiles @database))))))
