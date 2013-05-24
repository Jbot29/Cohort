(ns cohort.mongo
  (:require (monger core collection)))

(monger.core/connect! { :host "devint-internal.mapmyfitness.com" })

(defn events-collection [challenge-id]
  (str "challenge_" challenge-id "_events"))

(defn score-collection [challenge-id score-name]
  (str "challenge_" challenge-id "_" score-name "_scores"))

(defn each-daily-score [challenge-id fn]
  (binding [monger.core/*mongodb-database* (monger.core/get-db "score")]
    (let [cursor (monger.collection/find (score-collection challenge-id "daily_refuel"))]
      (while (.hasNext cursor)
        (fn (.next cursor))))))

(defn joined [user-id challenge-id]
  (binding [monger.core/*mongodb-database* (monger.core/get-db "score")]
    (let [result (monger.collection/find-one (events-collection challenge-id) { :entity_id user-id :entity_type "user_id" } [:joined])
          joined-date (.get result "joined")]
      (java.util.Date. (.getYear joined-date) (.getMonth joined-date) (.getDate joined-date)))))