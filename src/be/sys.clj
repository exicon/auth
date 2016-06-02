(ns sys
  (:require
    [auth.core]
    [system.repl :refer [system init start stop go reset]]
    [com.stuartsierra.component :as component]
    (system.components
      [jetty :refer [new-web-server]]
      [mongo :refer [new-mongo-db]])
    [environ.core :refer [env]]
    [auth0 :refer [new-auth0-client]]))

(defrecord RingHandler [mongo handler]
  component/Lifecycle
  (start [component]
    (let [mdb (get mongo :db)]
      (assoc component :handler (auth.core/ring-handler mdb))))
  (stop [component]))

(defn new-ring-handler []
  (map->RingHandler {}))


(defn dev []
  (component/system-map
    :mongo (new-mongo-db (env :db))
    :auth0 (new-auth0-client (env :auth0-url))
    :handler (component/using
               (new-ring-handler)
               [:mongo])
    :web (component/using
           (new-web-server (Integer. (env :http-port)))
           [:mongo :auth0 :handler])))
