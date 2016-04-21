(ns sys
  (:require
    [auth.core]
    [reloaded.repl :refer [system init start stop go reset]]
    [com.stuartsierra.component :as component]
    (system.components
      [jetty :refer [new-web-server]]
      [mongo :refer [new-mongo-db]])
    [environ.core :refer [env]]))

(defn dev []
  (component/system-map
    :mongo (new-mongo-db (env :db))
    :web (component/using
           (new-web-server (Integer. (env :http-port))
                           (auth.core/ring-handler))
           [:mongo])))
