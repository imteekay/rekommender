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
    (let [profile-id (atom nil)
          second-profile-id (atom nil)]
      (let [response (response-for service
                                   :get
                                   "/api/profiles"
                                   :headers {"Content-Type" "application/json"
                                             "Accept" "application/json"})]
        (is (= 200 (:status response)))
        (is (= [] (parse-response-body response))))

      (let [username "TK"
            profile {:username username}
            response (response-for service
                                   :post
                                   "/api/profiles"
                                   :body (json/encode profile)
                                   :headers {"Content-Type" "application/json"
                                             "Accept" "application/json"})
            parsed-response-body (parse-response-body response)]
        (reset! profile-id (:id parsed-response-body))
        (is (= username (:username parsed-response-body)))
        (is (= 201 (:status response))))

      (let [response (response-for service
                                   :get
                                   "/api/profiles"
                                   :headers {"Content-Type" "application/json"
                                             "Accept" "application/json"})
            parsed-response-body (parse-response-body response)]
        (is (= 200 (:status response)))
        (is (not-empty parsed-response-body))
        (is (= "TK" (-> parsed-response-body first :username))))

      (let [opt-in false
            profile {:opt-in opt-in}
            response (response-for service
                                   :put
                                   (str "/api/profiles/" @profile-id)
                                   :body (json/encode profile)
                                   :headers {"Content-Type" "application/json"
                                             "Accept" "application/json"})
            parsed-response-body (parse-response-body response)]
        (is (= 200 (:status response)))
        (is (= false (:opt-in parsed-response-body))))

      (let [username "Kazumi"
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
        (is (= 201 (:status response))))

      (let [first-profile-id @profile-id
            second-profile-id @second-profile-id
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
        (is (= 201 (:status response)))))))
