(ns cohort.reports
  (:use cohort.mongo)
  (:require clojure.pprint))

(defn joined-cohorts [challenge-id score-name score-keys]
  (let [^com.mongodb.DBCursor scores (score-cursor challenge-id score-name)
        score-fn (fn [^com.mongodb.DBObject score] (for [key score-keys] (.get score key)))
        map-fn (fn [^com.mongodb.DBObject score] { (joined (.get score "i") challenge-id) { (.get score "d") (score-fn score) } })
        add-fn (fn [a b] (doall (map + a b)))
        cohort-map (apply merge-with (concat [(partial merge-with add-fn)] (map map-fn scores)))
        all-dates (set (mapcat keys (vals cohort-map)))
        zeroes (for [key score-keys] 0)
        sort-fn (fn [x y] (let [c (compare (first x) (first y))] (if (= c 0) (compare (second x) (second y)) c)))]
    (sort sort-fn
      (for [[cohort cohort-data] cohort-map
            date all-dates]
        (if (contains? cohort-data date)
          (list cohort date (cohort-data date))
          (list cohort date zeroes))))))

(defn pprint-joined-cohorts [& args]
  (clojure.pprint/pprint
    (apply joined-cohorts args)))