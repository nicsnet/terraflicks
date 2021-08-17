(ns terraflicks.routes.services
  (:require
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.ring.coercion :as coercion]
    [reitit.coercion.spec :as spec-coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [terraflicks.middleware.formats :as formats]
    [terraflicks.messages :as messages]
    [taoensso.timbre :as timbre]
    [ring.util.http-response :refer :all]
    [clj-http.client :as client]
    [clojure.data.json :as json]
    [clojure.core.async :as a]
    [clojure.spec.alpha :as s]))

(def task-result
  {:payload_version int?
   :access_token string?
   :task_result_id string?
   :task_result_enforcement_level string?
   :task_result_callback_url string?
   :run_app_url string?
   :run_id string?
   :run_message string?
   :run_created_at string?
   :run_created_by string?
   :workspace_id string?
   :workspace_name string?
   :workspace_app_url string?
   :organization_name string?
   :plan_json_api_url string?
   :vcs_repo_url (s/nilable string?)
   :vcs_branch (s/nilable string?)
   :vcs_pull_request_url (s/nilable string?)
   :vcs_commit_url (s/nilable string?)})

(s/def ::message string?)

(defn task-result-payload [status]
  (json/write-str {:data {:type "task-results"
                          :attributes {:status status
                                       :message (messages/text)
                                       :url "https://www.terraform.io/"}}}))

(defn send-task-result [token url status]
  (when (not= token "test-token")
    (timbre/log :info "Sending task result request")
    (let [headers {:Authorization (str "Bearer " token)}]
      (client/patch url
                    {:body (task-result-payload status)
                     :headers headers
                     :content-type "application/vnd.api+json"
                     :accept :json}))))

(defn task-result-pass-handler [{{{:keys [access_token task_result_callback_url]} :body} :parameters}]
  ;; get access_token and callback_url and post back
  (send-task-result access_token task_result_callback_url "passed")
  (ok {:message "ok"}))

(defn task-result-fail-handler [{{{:keys [access_token task_result_callback_url]} :body} :parameters}]
  ;; get access_token and callback_url and post back
  (timbre/log :info "PATCH task result fail")
  (send-task-result access_token task_result_callback_url "failed")
  (ok {:message "ok"}))

(defn task-result-surprise-handler [{{{:keys [access_token task_result_callback_url]} :body} :parameters}]
  ;; get access_token and callback_url and post back
  (let [status (rand-nth ["passed" "failed"])]
    (timbre/log :info (str "PATCH task result " status))
    (send-task-result access_token task_result_callback_url status)
    (ok {:message "ok"})))

(defn task-result-error-404-handler [{{{:keys [access_token]} :body} :parameters}]
  (if (= access_token "test-token")
    (ok {:message "ok"})
    (not-found {:message "Not found"})))

(defn task-result-error-500-handler [{{{:keys [access_token]} :body} :parameters}]
  (if (= access_token "test-token")
    (ok {:message "ok"})
    (internal-server-error {:message "Something went wrong"})))

(defn task-result-timeout-handler [{{{:keys [access_token]} :body} :parameters}]
  (if (= access_token "test-token")
    (ok {:message "ok"})
    (Thread/sleep 600000)))

(defn service-routes []
  ["/api"
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 coercion/coerce-exceptions-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "terraflicks"
                         :description "An API to test run tasks"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
             {:url "/api/swagger.json"
              :config {:validator-url nil}})}]]

   ["/ping"
    {:get (constantly (ok {:message "pong"}))}]

   ["/run-tasks"
    {:swagger {:tags ["run-tasks"]}}

    ["/pass"
     {:post {:summary "use this endpoint to trigger a run task pass"
             :parameters {:body task-result}
             :responses {200 {:schema (s/keys :req-un [::message])}}
             :handler task-result-pass-handler}}]

    ["/fail"
     {:post {:summary "use this endpoint to trigger a run task failure"
             :parameters {:body task-result}
             :responses {200 {:schema (s/keys :req-un [::message])}}
             :handler task-result-fail-handler}}]

    ["/error-404"
     {:post {:summary "use this endpoint to trigger a run task error response 404"
             :parameters {:body task-result}
             :responses {200 {:schema (s/keys :req-un [::message])}}
             :handler task-result-error-404-handler}}]

    ["/error-500"
     {:post {:summary "use this endpoint to trigger a run task error response 500"
             :parameters {:body task-result}
             :responses {200 {:schema (s/keys :req-un [::message])}}
             :handler task-result-error-500-handler}}]

    ["/timeout"
     {:post {:summary "use this endpoint to let the task result timeout and never respond"
             :parameters {:body task-result}
             :responses {200 {:schema (s/keys :req-un [::message])}}
             :handler task-result-timeout-handler}}]

    ["/kinder-surprise"
     {:post {:summary "use this endpoint if you are undecided if a task result should pass or fail"
             :parameters {:body task-result}
             :responses {200 {:schema (s/keys :req-un [::message])}}
             :handler task-result-surprise-handler}}]]])
