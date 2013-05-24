(ns cohort.reports
  (:use cohort.mongo))

(defn add-daily-value [map day workouts joined]
  (let [new-value (+ (get (get map joined) day) workouts)]
    (assoc (get map joined) day new-value)))

(defn init-daily-value [map day workouts joined]
  (assoc map joined (sorted-map day workouts)))

(defn add-daily [map score challenge-id]
  (let [user-id (.get score "i")
        day (.get score "d")
        workouts (.get score "s4")
        user-joined (joined user-id challenge-id)]
    (if (contains? map user-joined)
      (add-daily-value map day workouts user-joined)
      (init-daily-value map day workouts user-joined))))

(defn daily-workout-cohorts [challenge-id]
  (let [result (atom (sorted-map))]
    (each-daily-score challenge-id (fn [score] (reset! result (add-daily @result score challenge-id))))
    result))