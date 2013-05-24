(ns cohort.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
	    [monger.core :as mg]
	    [clostache.parser :as clostache]))

(mg/connect!)
(mg/set-db! (mg/get-db "monger-test"))

(defn read-template [template-name]
  (slurp
      (str "resources/public/templates/" template-name ".mustache")))


(defn render-template [template-file params]
  (clostache/render (read-template template-file) params))

(defn index []
  (render-template "index" {:greeting "Bonjour"}))

(defroutes app-routes
  (GET "/" [] (index))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
