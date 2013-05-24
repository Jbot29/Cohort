(ns cohort.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [monger.core :as mg]
            [clostache.parser :as clostache]
            [ring.util.response :as response]
	    [cohort.reports :as reports]))

(defn read-template [template-name]
  (slurp
      (str "resources/public/templates/" template-name ".mustache")))


(defn render-template [template-file params]
  (clostache/render (read-template template-file) params))

(defn index []
  (render-template "index" {}))

(defn cohort-name
      [x]
      (let [v (clojure.string/split (str x) #"\s")]
      	   (str (nth v 1) (nth v 2))))

(defn cohort-date
      [date]
      (.format (java.text.SimpleDateFormat. "MM/dd/yy") date))

(defn all-dates
      [cohorts]
      (list (set(for [[cohort workouts] cohorts
           [workout-date workout-count] workouts]
           (cohort-date workout-date)))))

(defn build-report-data
      [challenge_id]
      (let [cohorts (reports/daily-workout-cohorts challenge_id)]
      	   (for [[cohort workouts] cohorts
	   	[workout-date workout-count] workouts]
	   	(list (cohort-name cohort) workout-count (cohort-date workout-date)))))

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
      (write-report (build-report (build-report-data (:cha_id params))))
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
