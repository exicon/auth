(ns auth.signup-test
  (:use midje.sweet)
  (:require
    [auth.core :refer [signup
                       create-user
                       auth0-user-exists?]]))

(facts "Signup"
  (fact "succesfully"
    (signup ..sys.. ..auth0-user-id..)
    => {:user-eid ..user-eid..
        :user-id  ..auth0-user-id..}
    (provided
      (create-user ..auth0-user-id..) => ..user-eid..
      (auth0-user-exists? ..auth0-user-id..) => true))

  (fact "fails if auth0 user doesn't exist"
    (signup ..sys.. ..auth0-user-id..)
    => (throws #"Auth0 user doesn't exist")
    (provided
      (auth0-user-exists? ..auth0-user-id..) => false))

  (fact "fails if auth0 user id is missing"
    (signup ..sys.. nil)
    => (throws #"Auth0 user doesn't exist")
    (provided
      (auth0-user-exists? nil) => false)))

