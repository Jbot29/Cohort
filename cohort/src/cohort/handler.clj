(ns cohort.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
	    [monger.core :as mg]))

(mg/connect!)
(mg/set-db! (mg/get-db "monger-test"))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
