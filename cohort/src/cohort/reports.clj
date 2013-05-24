(ns cohort.reports
  (:use cohort.mongo))

(defn add-daily-value [map day workouts joined]
  (let [cohort-map (get map joined)]
    (if (contains? cohort-map day)
      (let [new-value (+ (get (get map joined) day) workouts)]
        (assoc map joined (assoc cohort-map day new-value)))
      (assoc map joined (assoc cohort-map day workouts)))))

(defn init-daily-value [map day workouts joined]
  (assoc map joined (sorted-map day workouts)))

(defn add-daily [map score challenge-id]
  (let [user-id (.get score "i")
        day (.get score "d")
        workouts (.get score "s1")
        user-joined (joined user-id challenge-id)]
    (if (contains? map user-joined)
      (add-daily-value map day workouts user-joined)
      (init-daily-value map day workouts user-joined))))

(defn daily-workout-real [challenge-id cursor accum]
  (println accum)
  (if (.hasNext cursor)
    (recur challenge-id cursor (add-daily accum (.next cursor) challenge-id))
    accum))

(defn daily-workout-cohorts [challenge-id]
  (daily-workout-real challenge-id (daily-score-cursor challenge-id "daily_cdphp") (sorted-map)))
