(ns cohort.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
	    [monger.core :as mg]
	    [clostache.parser :as clostache]
	    [ring.util.response :as response]))

(mg/connect!)
(mg/set-db! (mg/get-db "monger-test"))

(defn read-template [template-name]
  (slurp
      (str "resources/public/templates/" template-name ".mustache")))


(defn render-template [template-file params]
  (clostache/render (read-template template-file) params))

(defn index []
  (render-template "index" {}))


(defn build-report-data
      []
      (list 
      	    (list "Group1" "37" "05/23/12")
	    (list "Group2" "0" "05/23/12")
	    (list "Group3" "0" "05/23/12")
            (list "Group1" "22" "05/24/12")
            (list "Group2" "10" "05/24/12")
            (list "Group3" "0" "05/24/12")
))

(defn build-report
      [report-data]
      (str "key,value,date\n" (clojure.string/join "\n" (map (fn [x] (clojure.string/join "," x)) report-data))))

(defn write-report
      [report]
      (with-open [wrtr (clojure.java.io/writer "resources/public/data.csv")]
        (.write wrtr report)))

(defn gen-report 
      [params]
      (println (:cha_id params))
      (write-report (build-report (build-report-data)))
      (response/redirect "/"))

(defn gen-report-page
      []
      (render-template "gen-report" {}))

(defroutes app-routes
  (GET "/" [] (index))
  (GET "/report" [] (gen-report-page))
  (POST "/report" {params :params} (gen-report params))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
