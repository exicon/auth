(ns auth.core
  (:require
    [clojure.string :as str]
    [environ.core :refer [env]]
    [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.middleware.session :refer [wrap-session]]
    [castra.middleware :refer [wrap-castra wrap-castra-session]]
    [castra.core :refer [*session* defrpc]]
    [ring.util.response :refer [not-found charset]]
    [debux.core :as dx]))

(defn wrap-debug [handler]
  (fn [req]
    (dx/dbg req "REQUEST")
    (dx/dbg (handler req) "RESPONSE")))

(defn ring-handler []
  (-> (constantly (not-found "Unhandled request"))
      (wrap-defaults api-defaults)
      (wrap-castra 'api)
      (wrap-castra-session "0123456789012345")
      (wrap-cors (re-pattern (str "^" (env :frontend-url) "/?$")))
      (wrap-debug)))
