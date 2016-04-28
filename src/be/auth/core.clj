(ns auth.core
  (:require
    [clojure.string :as s]
    [environ.core :refer [env]]
    [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.middleware.session :refer [wrap-session]]
    [castra.middleware :refer [wrap-castra wrap-castra-session]]
    [castra.core :refer [*session* defrpc]]
    [castra.middleware :as castra]
    [ring.util.response :refer [not-found charset]]
    [debux.core :as dx]
    [buddy.core.codecs.base64 :as base64]
    [buddy.auth :refer [authenticated? throw-unauthorized]]
    [buddy.auth.backends.token :refer [jws-backend]]
    [buddy.auth.backends.session :refer [session-backend]]
    [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
    [buddy.sign.jws :as jws]
    [node-mongo-store :refer [node-mongo-store]]))

(defn wrap-debug [handler]
  (fn [req]
    (dx/dbg req "REQUEST")
    (dx/dbg (handler req) "RESPONSE")))

(defn auth0-backend []
  (jws-backend {:secret               (base64/decode (env :auth0-client-secret))
                :token-name           "Bearer"}))

(defn wrap-not-authenticated [handler]
  (fn [req]
    (if (authenticated? req)
      (handler req)
      {:status  200
       :headers (castra/headers req
                                {"X-Castra-Tunnel" "transit"}
                                {"Content-Type" "application/json"})
       :body    (castra/response nil req {:error {:message "Unauthorized"
                                                  :data    :unauthorized}})
       :session @*session*})))

(defn ring-handler [db]
  (-> (constantly (not-found "Unhandled request"))
      (wrap-defaults api-defaults)
      (wrap-castra 'api)
      (wrap-castra-session "0123456789012345")
      (wrap-not-authenticated)
      (wrap-authentication (auth0-backend) (session-backend))
      (wrap-debug)
      (wrap-session {:cookie-name "appboard.v1"
                     :store (node-mongo-store db "sessions")})
      (wrap-cors (re-pattern (str "^" (env :frontend-url) "/?$")))
      ))
