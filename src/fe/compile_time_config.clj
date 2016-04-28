(ns compile-time-config
  (:require [environ.core]))

(defmacro env [& [key]]
  (if key
    (environ.core/env key)
    environ.core/env))
