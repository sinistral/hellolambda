
(ns hellolambda.core
  (:require [cljs.nodejs :as nodejs]))

;;; ----------------------------------------------------------------------- ;;;

(defn- ->jsonstr
  [x]
  (.stringify js/JSON x))

(defn- wrap-invocation
  [f]
  (try
    {:succeeded? true :failed? false :result (f)}
    (catch :default e
      {:suceeded false :failed? true :result e})))

(defn- unpack-input
  [x]
  [(js->clj x :keywordize-keys true)])

(defn- pack-output
  [x]
  (-> x (clj->js) (->jsonstr)))

(defn- respond
  [result callback]
  (println result)
  (if (:succeeded? result)
    (callback nil (pack-output (:result result)))
    (callback (:result result) nil)))

;;; ----------------------------------------------------------------------- ;;;

(defn- λ
  [event]
  (str "hello, " (:name event)))

(defn ^{:export true} main
  [event context callback]
  (-> {:available-execution-time
       {:quantity (.getRemainingTimeInMillis context) :units "ms"}
       :began-at
       (.toISOString (js/Date.))}
      (merge (wrap-invocation #(apply λ (unpack-input event))))
      (assoc :ended-at (.toISOString (js/Date.)))
      (respond callback))
  200)

(set! *main-cli-fn* identity)
