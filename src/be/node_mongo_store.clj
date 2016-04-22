(ns node-mongo-store
  (:require
    [ring.middleware.session :as session]
    [ring.middleware.session.store :as ring-session-store]
    [monger.collection :as mc :refer [find-one-as-map]]
    [clojure.data.json :as json])
  (:import
    [com.mongodb DB]
    ring.middleware.session.store.SessionStore))

; Implementation is based on
; https://github.com/michaelklishin/monger/blob/master/src/clojure/monger/ring/session_store.clj#L88-L111

(defn session-id [key]
  (second (clojure.string/split (or key "") #"s:|\.")))

(defrecord NodeMongoSessionStore [^DB db ^String session-collection])

(extend-protocol ring-session-store/SessionStore
  NodeMongoSessionStore

  (read-session
    [store key]
    (let [sid (second (clojure.string/split (or key "") #"s:|\."))]
      (if-let [session-map (and key (find-one-as-map
                            (.db store)
                            (.session-collection store)
                            {:_id sid}))]
        (let [{:as session {:keys [firstName lastName email]} :user }
              (json/read-str (:session session-map) :key-fn keyword)]
          (assoc session :identity email))
        {})))

  ; User switching support
  #_(write-session
      [store key data]
      (let [sid (session-id key)]
        (prn "-------------------" sid)
        (prn :write-session data)
        ; 1. key and key exists in mongo db
        ; 2. diff of session and mongo-session data is not nil
        (mc/update (.db store)
                   (.session-collection store)
                   {:_id sid}
                   {"$set" {:session (json/write-str data)}})
        key))

  (write-session [store key data] key)

  (delete-session [store key] nil))

(defn node-mongo-store
  [^DB db ^String session-collection]
  (NodeMongoSessionStore. db session-collection))
