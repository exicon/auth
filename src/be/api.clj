(ns api
  (:require
    [environ.core :refer [env]]
    [castra.core :refer [defrpc *request* *session*]]
    [ring.util.request :refer [body-string]]
    [buddy.sign.jws :as jws]
    [buddy.core.codecs.base64 :as base64]
    [debux.core :as dx]))

(defn authorized? [& args]
  (:identity *request*))

(defrpc get-current-user []
  ;{:rpc/pre (authorized?)}
  (:identity *request*)
  ;(ex-info "Error" {})
  )
