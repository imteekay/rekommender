(ns nukr.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [cheshire.core :as json]
            [nukr.service :as service]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(defn parse-response-body
  [response]
  (json/parse-string (:body response) true))

(deftest api-integration-test
  (testing "API Integration Test"
    (let [first-profile-id (atom nil)
          second-profile-id (atom nil)
          third-profile-id (atom nil)
          forth-profile-id (atom nil)
          fifth-profile-id (atom nil)
          sixth-profile-id (atom nil)]

      (testing "Get list of profiles"
        (let [response (response-for service
                                     :get
                                     "/api/profiles"
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})]
          (is (= 200 (:status response)))
          (is (= [] (parse-response-body response)))))

      (testing "Creates new profile"
        (let [username "Rachel"
              profile {:username username}
              response (response-for service
                                     :post
                                     "/api/profiles"
                                     :body (json/encode profile)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (reset! first-profile-id (:id parsed-response-body))
          (is (= username (:username parsed-response-body)))
          (is (= 201 (:status response)))))

      (testing "Gets list of profiles"
        (let [response (response-for service
                                     :get
                                     "/api/profiles"
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (is (= 200 (:status response)))
          (is (not-empty parsed-response-body))
          (is (= "Rachel" (-> parsed-response-body first :username)))))

      (testing "Creates new profile"
        (let [username "Monica"
              profile {:username username}
              response (response-for service
                                     :post
                                     "/api/profiles"
                                     :body (json/encode profile)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (reset! second-profile-id (:id parsed-response-body))
          (is (= username (:username parsed-response-body)))
          (is (= 201 (:status response)))))

      (testing "Creates new profile"
        (let [username "Phoebe"
              profile {:username username}
              response (response-for service
                                     :post
                                     "/api/profiles"
                                     :body (json/encode profile)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (reset! third-profile-id (:id parsed-response-body))
          (is (= username (:username parsed-response-body)))
          (is (= 201 (:status response)))))

      (testing "Creates new profile"
        (let [username "Joey"
              profile {:username username}
              response (response-for service
                                     :post
                                     "/api/profiles"
                                     :body (json/encode profile)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (reset! forth-profile-id (:id parsed-response-body))
          (is (= username (:username parsed-response-body)))
          (is (= 201 (:status response)))))

      (testing "Creates new profile"
        (let [username "Chandler"
              profile {:username username}
              response (response-for service
                                     :post
                                     "/api/profiles"
                                     :body (json/encode profile)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (reset! fifth-profile-id (:id parsed-response-body))
          (is (= username (:username parsed-response-body)))
          (is (= 201 (:status response)))))

      (testing "Creates new profile"
        (let [username "Ross"
              profile {:username username}
              response (response-for service
                                     :post
                                     "/api/profiles"
                                     :body (json/encode profile)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (reset! sixth-profile-id (:id parsed-response-body))
          (is (= username (:username parsed-response-body)))
          (is (= 201 (:status response)))))

      (testing "Updates the Ross profile"
        (let [opt-in false
              profile {:opt-in opt-in}
              response (response-for service
                                     :put
                                     (str "/api/profiles/" @sixth-profile-id)
                                     :body (json/encode profile)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (is (= 200 (:status response)))
          (is (= false (:opt-in parsed-response-body)))))

      (testing "Connects Rachel and Phoebe"
        (let [first-profile-id @first-profile-id
              second-profile-id @third-profile-id
              profiles-ids {:first-profile-id first-profile-id
                            :second-profile-id second-profile-id}
              response (response-for service
                                     :post
                                     "/api/connections"
                                     :body (json/encode profiles-ids)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (is (= first-profile-id (:first-profile-id parsed-response-body)))
          (is (= second-profile-id (:second-profile-id parsed-response-body)))
          (is (= 201 (:status response)))))

      (testing "Connects Rachel and Joey"
        (let [first-profile-id @first-profile-id
              second-profile-id @forth-profile-id
              profiles-ids {:first-profile-id first-profile-id
                            :second-profile-id second-profile-id}
              response (response-for service
                                     :post
                                     "/api/connections"
                                     :body (json/encode profiles-ids)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (is (= first-profile-id (:first-profile-id parsed-response-body)))
          (is (= second-profile-id (:second-profile-id parsed-response-body)))
          (is (= 201 (:status response)))))

      (testing "Connects Monica and Phoebe"
        (let [first-profile-id @second-profile-id
              second-profile-id @third-profile-id
              profiles-ids {:first-profile-id first-profile-id
                            :second-profile-id second-profile-id}
              response (response-for service
                                     :post
                                     "/api/connections"
                                     :body (json/encode profiles-ids)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (is (= first-profile-id (:first-profile-id parsed-response-body)))
          (is (= second-profile-id (:second-profile-id parsed-response-body)))
          (is (= 201 (:status response)))))

      (testing "Connects Monica and Joey"
        (let [first-profile-id @second-profile-id
              second-profile-id @forth-profile-id
              profiles-ids {:first-profile-id first-profile-id
                            :second-profile-id second-profile-id}
              response (response-for service
                                     :post
                                     "/api/connections"
                                     :body (json/encode profiles-ids)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (is (= first-profile-id (:first-profile-id parsed-response-body)))
          (is (= second-profile-id (:second-profile-id parsed-response-body)))
          (is (= 201 (:status response)))))

      (testing "Connects Chandler and Joey"
        (let [first-profile-id @fifth-profile-id
              second-profile-id @forth-profile-id
              profiles-ids {:first-profile-id first-profile-id
                            :second-profile-id second-profile-id}
              response (response-for service
                                     :post
                                     "/api/connections"
                                     :body (json/encode profiles-ids)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (is (= first-profile-id (:first-profile-id parsed-response-body)))
          (is (= second-profile-id (:second-profile-id parsed-response-body)))
          (is (= 201 (:status response)))))

      (testing "Connects Ross and Phoebe"
        (let [first-profile-id @sixth-profile-id
              second-profile-id @third-profile-id
              profiles-ids {:first-profile-id first-profile-id
                            :second-profile-id second-profile-id}
              response (response-for service
                                     :post
                                     "/api/connections"
                                     :body (json/encode profiles-ids)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (is (= first-profile-id (:first-profile-id parsed-response-body)))
          (is (= second-profile-id (:second-profile-id parsed-response-body)))
          (is (= 201 (:status response)))))

      (testing "Connects Ross and Joey"
        (let [first-profile-id @sixth-profile-id
              second-profile-id @forth-profile-id
              profiles-ids {:first-profile-id first-profile-id
                            :second-profile-id second-profile-id}
              response (response-for service
                                     :post
                                     "/api/connections"
                                     :body (json/encode profiles-ids)
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (is (= first-profile-id (:first-profile-id parsed-response-body)))
          (is (= second-profile-id (:second-profile-id parsed-response-body)))
          (is (= 201 (:status response)))))

      (testing "Gets profiles suggestion for Rachel"
        (let [response (response-for service
                                     :get
                                     (str "/api/profiles/" @first-profile-id "/suggestions")
                                     :headers {"Content-Type" "application/json"
                                               "Accept" "application/json"})
              parsed-response-body (parse-response-body response)]
          (is (= ["Monica" "Chandler"] (map #(:username %) parsed-response-body)))
          (is (= 200 (:status response))))))))
