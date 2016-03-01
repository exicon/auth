(ns compile-time-config
  (:require [environ.core]))

(defmacro env [key]
  (environ.core/env key))
