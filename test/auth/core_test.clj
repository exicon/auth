(ns auth.core-test
  (:use midje.sweet)
  (:require
    [auth.core]

    [buddy.sign.jws :as jws]
    [buddy.core.codecs.base64 :as base64]
    [ring.mock.request :as mock]
    [debux.core :as dx]
    [clojure.data.json :as json]
    [cognitect.transit :as t]
    [clojure.java.io :as io])
  (:import
    [java.net URLEncoder]
    (java.io ByteArrayInputStream)))

(defn url-encode [s]
  (URLEncoder/encode s "UTF-8"))

(defn json-body [resp]
  (-> resp :body (json/read-str :key-fn keyword)))

(defn transit-body [resp]
  (-> resp
      :body
      (.getBytes)
      (ByteArrayInputStream.)
      (t/reader :json) t/read))

(defn new-app []
  (auth.core/ring-handler ..db..))

(facts "Signup request"
  (let [app (new-app)
        auth0-user-id (url-encode "auth0|1234567890")]
    (-> (mock/request :post (str "/signup/" auth0-user-id))
        (app)
        json-body)
    => (contains {:user-eid anything
                  :user-id anything})))

(facts "Castra request"
  (let [app (new-app)]
    (-> (mock/request :post "/")
        (mock/content-type "application/transit+json")
        (mock/header "x-castra-tunnel" "transit")
        (mock/header "x-castra-csrf" "true")
        (mock/body "[\"~$api/get-current-user\"]")
        (app)
        transit-body)
    => {:ok nil}))
