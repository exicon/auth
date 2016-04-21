(ns api
  (:require
    [environ.core :refer [env]]
    [castra.core :refer [defrpc *request* *session*]]
    [ring.util.request :refer [body-string]]
    [buddy.sign.jws :as jws]
    [buddy.core.codecs.base64 :as base64]))

(defn authorized? [& args]
  (when-let [jwt (-> *request* :headers (get "authorization"))]
    (jws/unsign jwt (base64/decode (env :auth0-client-secret)))))

(defrpc get-current-user []
  {:rpc/pre (authorized?)}
  {:req  (dissoc *request* :body)
   :sess (str @*session*)})
