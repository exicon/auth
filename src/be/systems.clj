(ns systems
  (:require
    [reloaded.repl :refer [system init start stop go reset]]
    [com.stuartsierra.component :as component]
    (system.components
      [jetty :refer [new-web-server]]
      [mongo :refer [new-mongo-db]])
    [environ.core :refer [env]]
    #_[auth0 :refer [new-auth0-client]]))

(defrecord TestComponent [name id]
  component/Lifecycle
  (start [c]
    (println "Test component started"))
  (stop [c]
    (println "Test component stopped")))

(defn dev []
  (component/system-map
    ;:test-comp (map->TestComponent {:name "My name" :id "1234"})
    ;:comp2 (map->TestComponent {:name "My name 2" :id "123456"})
    ))
