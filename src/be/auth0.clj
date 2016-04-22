(ns auth0
  (:require [clojure.data.json :as json]
            [org.httpkit.client :as http]))

(defprotocol IAuth0Client
  (user-by-jwt [_ jwt])
  (user-by-access-token [_ access-token]))

(defn http-req [& {:as opts}]
  (let [{:keys [status body error] :as resp} @(http/request opts)]
    (if (= status 200)
      (json/read-str body)
      (throw (ex-info (str "Can't get user by access token."
                           (when status (str " HTTP status: " status))
                           (when error (str " Cause: " (.getMessage error))))
                      resp)))))

(defrecord Auth0Client [url]
  IAuth0Client

  (user-by-jwt [_ jwt]
    (http-req :url (str url "/tokeninfo")
              :method :post
              :headers {"Content-Type" "application/json"}
              :query-params {"id_token" jwt}))

  (user-by-access-token [_ access-token]
    (http-req :url (str url "/userinfo")
              :method :get
              :headers {"Authorization" (str "Bearer " access-token)})))

(defn new-auth0-client [url]
  (->Auth0Client url))



