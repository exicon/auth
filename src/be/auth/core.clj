(ns auth.core
  (:require
    [clojure.string :as s]
    [environ.core :refer [env]]
    [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
    [ring.middleware.conditional]
    [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.middleware.session :refer [wrap-session]]
    [ring.util.response :refer [not-found charset]]
    [castra.middleware :refer [wrap-castra wrap-castra-session]]
    [castra.core :refer [*session* defrpc]]
    [castra.middleware :as castra]
    [debux.core :as dx]
    [buddy.core.codecs.base64 :as base64]
    [buddy.auth :refer [authenticated? throw-unauthorized]]
    [buddy.auth.backends.token :refer [jws-backend]]
    [buddy.auth.backends.session :refer [session-backend]]
    [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
    [buddy.sign.jws :as jws]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [compojure.handler :as handler]

    [node-mongo-store :refer [node-mongo-store]]
    ))

(defn auth0-user-exists? [auth0-user-id]
  true)

(defn create-user [auth0-user-id]
  (gensym "eid-"))

(defn signup [sys auth0-user-id]
  (if (auth0-user-exists? auth0-user-id)
    {:user-eid (create-user auth0-user-id)
     :user-id  auth0-user-id}
    (throw (ex-info "Auth0 user doesn't exist"
                    {:error :user-not-found}))))

(defn auth0-register [req]
  (println "auth0-register" req)
  )

(defroutes REST-api
  (POST "/auth0/register" req (auth0-register req))
  (POST "/signup/:user-id" [user-id]
    (ring.util.response/response (signup {} user-id)))
  (route/not-found "<h1>Path is unrecognized</h1>"))

(defn wrap-debug [handler]
  (fn [req]
    (dx/dbg req "REQUEST")
    (dx/dbg (handler req) "RESPONSE")))

(defn auth0-backend []
  (jws-backend {:secret     (base64/decode (env :auth0-client-secret))
                :token-name "Bearer"}))

(defn REST-backend []
  (jws-backend {:secret     (base64/decode "DEV_API_CLIENT_SECURE_SECRET")
                :token-name "Bearer"}))

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

(defn wrap-continue-unless-responded [handler conditional-handler]
  (fn [req]
    (if-let [resp (conditional-handler req)]
      resp
      (handler req))))

(defn castra? [req]
  (-> req :headers (get "x-castra-tunnel")))

(defn ring-handler [db]
  (-> (handler/api REST-api)
      (wrap-json-body)
      (wrap-json-response)

      (ring.middleware.conditional/if
        castra? #(wrap-castra % 'api))
      (wrap-castra-session "0123456789012345")

      (wrap-not-authenticated)
      (wrap-defaults api-defaults)
      ;(wrap-debug)
      (wrap-authentication (auth0-backend) (REST-backend) (session-backend))
      (wrap-session {:cookie-name "appboard.v1"
                     :store       (node-mongo-store db "sessions")})

      (wrap-cors (re-pattern (str "^" (env :frontend-url) "/?$")))
      ))

