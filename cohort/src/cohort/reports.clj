(ns cohort.reports
  (:use cohort.mongo)
  (:require clojure.pprint))

(defn scoreinit [^com.mongodb.DBObject score score-date score-keys]
  (let [result (java.util.HashMap.)]
    (.put result score-date (scoreget score score-keys))
    result))

(defn scoreget [^com.mongodb.DBObject score score-keys]
  (for [key score-keys]
    (.get score key)))

(defn scoreadd [score-one score-two]
  (for [[s-one s-two] (map vector score-one score-two)]
    (+ s-one s-two)))

(defn update [^java.util.HashMap map ^com.mongodb.DBObject score challenge-id score-keys]
  (let [score-date (.get score "d")
        user-id (.get score "i")
        joined-date (joined user-id challenge-id)]
    (if (.containsKey map joined-date)
      (let [^java.util.HashMap joined-date-data (.get map joined-date)]
        (if (.containsKey joined-date-data score-date)
          (.put joined-date-data score-date (scoreadd (.get joined-date-data score-date) (scoreget score score-keys)))
          (.put joined-date-data score-date (scoreget score score-keys))))
      (.put map joined-date (scoreinit score score-date score-keys)))))

(defn find-all-dates [^java.util.HashMap map]
  (set
    (for [[cohort cohort-data] map
          [date scores] cohort-data]
      date)))

(defn listify [^java.util.HashMap map score-keys]
  (let [all-dates (find-all-dates map)
        zeroes (for [key score-keys] 0)]
    (for [[cohort ^java.util.HashMap cohort-data] map
          date all-dates]
      (if (.containsKey cohort-data date)
        (list cohort date (.get cohort-data date))
        (list cohort date zeroes)))))

(defn cmp [list-one list-two]
  (let [cohort-one (first list-one)
        cohort-two (first list-two)
        cohort-compare (compare cohort-one cohort-two)]
    (if (= cohort-compare 0)
      (compare (second list-one) (second list-two))
      cohort-compare)))

(defn joined-cohorts [challenge-id score-name score-keys]
  (let [result (java.util.HashMap.)
        ^com.mongodb.DBCursor cursor (score-cursor challenge-id score-name)]
    (while (.hasNext cursor)
      (update result (.next cursor) challenge-id score-keys))
    (sort cmp (listify result score-keys))))

(defn print-joined-cohorts [& args]
  (clojure.pprint/pprint (apply joined-cohorts args)))