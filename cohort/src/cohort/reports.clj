(ns cohort.reports
  (:use cohort.mongo))

(defn update! [map score challenge-id score-key]
  (let [workout-date (.get score "d")
        user-id (.get score "i")
        workouts (.get score score-key)
        cohort-date (joined user-id challenge-id)]
    (if (contains? map cohort-date)
      (let [workout-map (get map cohort-date)]
        (if (contains? workout-map workout-date)
          (assoc! workout-map workout-date (+ workouts (get workout-map workout-date)))
          (assoc! workout-map workout-date workouts)))
      (assoc! map cohort-date (transient { workout-date workouts })))))

(defn workout-cohorts [challenge-id score-name score-key]
  (loop [cursor (score-cursor challenge-id score-name)
         result (transient (hash-map))]
    (if (not (.hasNext cursor))
      (persistent! result)
      (recur cursor (update! result (.next cursor) challenge-id score-key)))))
