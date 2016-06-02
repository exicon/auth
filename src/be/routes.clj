(ns routes
  (:require
    [compojure.core :refer [defroutes GET POST]]
    ))

(defn auth0-register [req]
  (println "auth0-register" req))

(defroutes routes
  (POST "/auth0/register" req (auth0-register req)))
