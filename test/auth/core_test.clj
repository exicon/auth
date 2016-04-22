(ns auth.core-test
  (:use midje.sweet)
  (:require
    [auth.core]
    [ring.util.request]
    [buddy.sign.jws :as jws]
    [buddy.core.codecs.base64 :as base64]
    [ring.mock.request :as mock]))

(def unauthorized-resp
  "401 request must have a WWW-Authenticate header, which wont be understood
   by the browser but can be used for debugging."
  {:status  401
   :headers {"WWW-Authenticate" (str "JWT location=https://exicon-dev.auth0.com/authorize/"
                                     "?response_type=token&"
                                     "client_id=lInsuIpRVolXblEqemVb6eH4ahgKFszf&"
                                     "redirect_uri=http://localhost:9000&state=&scope=openid")}})

(facts "wrap-auth"
  (fact "authorized - auth0"
    ((auth.core/wrap-auth identity ..auth0-client-secret..)
      {:headers {"Authorization" (str "Bearer JWT")}})
    => (contains
         {:session (contains
                     {:user-id ..subject..})})
    (provided
      (base64/decode ..auth0-client-secret..) => ..secret-bytes..
      (jws/unsign "JWT" ..secret-bytes..) => {:sub ..subject..}))

  #_(fact "authorized - legacy"
    ((auth.core/wrap-auth identity ..auth0-client-secret..)
      {:cookie ..cookie..})
    => (contains
         {:session (contains {:user-id ..oid..})}))

  (fact "unauthorized - auth0"
    ((auth.core/wrap-auth identity ..auth0-client-secret..)
      {:body "unauthorized rpc call"})
    => unauthorized-resp
    (provided
      (base64/decode ..auth0-client-secret..) => ..secret-bytes..
      (jws/unsign "JWT" ..secret-bytes..) => {:sub ..subject..})))
