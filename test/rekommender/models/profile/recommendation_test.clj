(ns rekommender.models.profile.recommendation-test
  (:require [clojure.test :refer :all]
            [rekommender.models.profile.recommendation :refer :all]))

(deftest connected?-test
  (testing "Is the profile in this connection?"
    (let [profile {:id 1 :username "TK" :opt-in true}
          connection {:id 1 :first-profile-id 1 :second-profile-id 2}]
      (is (connected? profile connection)))

    (let [profile {:id 1 :username "TK" :opt-in true}
          connection {:id 1 :first-profile-id 2 :second-profile-id 1}]
      (is (connected? profile connection)))

    (let [profile {:id 1 :username "TK" :opt-in true}
          connection {:id 1 :first-profile-id 2 :second-profile-id 3}]
      (is (= false (connected? profile connection))))))

(deftest get-profile-connection-id-test
  (testing "Gets the profile connection"
    (let [profile {:id 1 :username "TK" :opt-in true}
          connection {:id 1 :first-profile-id 1 :second-profile-id 2}]
      (is (= 2 (get-profile-connection-id profile connection))))

    (let [profile {:id 1 :username "TK" :opt-in true}
          connection {:id 1 :first-profile-id 2 :second-profile-id 1}]
      (is (= 2 (get-profile-connection-id profile connection))))))

(deftest get-all-connections-ids-test
  (testing "Gets all connections for a specific profile"
    (let [profile {:id 1 :username "TK" :opt-in true}
          connections [{:id 1 :first-profile-id 1 :second-profile-id 2}
                       {:id 2 :first-profile-id 1 :second-profile-id 3}
                       {:id 3 :first-profile-id 4 :second-profile-id 1}
                       {:id 4 :first-profile-id 5 :second-profile-id 1}]]
      (is (= [2 3 4 5] (get-all-connections-ids profile connections))))))

(deftest no-connection-between-profiles?-test
  (testing "Verifies if there is no connection between two profiles"
    (let [profile {:id 1 :username "TK" :opt-in true}
          connections [{:id 1 :first-profile-id 1 :second-profile-id 2}
                       {:id 2 :first-profile-id 1 :second-profile-id 3}
                       {:id 3 :first-profile-id 4 :second-profile-id 1}
                       {:id 4 :first-profile-id 5 :second-profile-id 1}]]
      (let [other-profile {:id 2 :username "Kazumi" :opt-in true}]
        (is (= false (no-connection-between-profiles? profile connections other-profile))))

      (let [other-profile {:id 6 :username "Kaio" :opt-in true}]
        (is (no-connection-between-profiles? profile connections other-profile))))))

(deftest not-the-same-profile?-test
  (testing "Compares if the profiles are the same"
    (let [profile {:id 1 :username "TK" :opt-in true}
          other-profile {:id 1 :username "TK" :opt-in true}]
      (is (= false (not-the-same-profile? profile other-profile))))

    (let [profile {:id 1 :username "TK" :opt-in true}
          other-profile {:id 2 :username "Kazumi" :opt-in true}]
      (is (not-the-same-profile? profile other-profile)))))

(deftest count-connection-matches-test
  (testing "Counts the number of connections that match between two profiles"
    (let [profile {:id 1 :username "TK" :opt-in true}
          connections [{:id 1 :first-profile-id 1 :second-profile-id 3}
                       {:id 2 :first-profile-id 1 :second-profile-id 4}
                       {:id 3 :first-profile-id 2 :second-profile-id 3}
                       {:id 4 :first-profile-id 2 :second-profile-id 4}
                       {:id 5 :first-profile-id 5 :second-profile-id 3}]]
      (let [other-profile {:id 2 :username "Kazumi" :opt-in true}]
        (is (= 2 (count-connection-matches profile connections other-profile))))

      (let [other-profile {:id 5 :username "Kaio" :opt-in true}]
        (is (= 1 (count-connection-matches profile connections other-profile))))

      (let [other-profile {:id 3 :username "Marie" :opt-in true}]
        (is (= 0 (count-connection-matches profile connections other-profile)))))))

(deftest profiles-suggestion-test
  (testing "Gets profiles suggestion for a specific profile"
    (let [profiles [{:id 1 :username "Rachel"   :opt-in true}
                    {:id 2 :username "Monica"   :opt-in true}
                    {:id 3 :username "Phoebe"   :opt-in true}
                    {:id 4 :username "Joey"     :opt-in true}
                    {:id 5 :username "Chandler" :opt-in true}
                    {:id 6 :username "Ross"     :opt-in false}]
          connections [{:id 1 :first-profile-id 1 :second-profile-id 3}
                       {:id 2 :first-profile-id 1 :second-profile-id 4}
                       {:id 3 :first-profile-id 2 :second-profile-id 3}
                       {:id 4 :first-profile-id 2 :second-profile-id 4}
                       {:id 5 :first-profile-id 5 :second-profile-id 3}
                       {:id 6 :first-profile-id 6 :second-profile-id 3}
                       {:id 7 :first-profile-id 6 :second-profile-id 4}]
          rachel (first profiles)
          monica (second profiles)
          chandler (nth profiles 4)]
      (is (= [monica chandler] (profiles-suggestion rachel profiles connections))))))
