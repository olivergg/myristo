(ns myristo.handlers
  (:require
    [re-frame.core :refer [reg-event-db ->interceptor reg-event-fx]]
    [clojure.spec.alpha :as s]
    [myristo.db :as db :refer [app-db]]))

;; -- Interceptors ----------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/develop/docs/Interceptors.md
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (->interceptor
        :id :validate-spec
        :after (fn [context]
                 (let [db (-> context :effects :db)]
                   (check-and-throw ::db/app-db db)
                   context)))
    ->interceptor))

;; -- Handlers --------------------------------------------------------------

(reg-event-db
  :initialize-db
  [validate-spec]
  (fn [_ _]
    app-db))

(reg-event-db
  :set-greeting
  [validate-spec]
  (fn [db [_ value]]
    (assoc db :greeting value)))


(reg-event-db
  :compute-occupancy
  validate-spec
  (fn [db [_ myval]]
    (let [currentresto (-> db :restaurants (myval))
          url (:url currentresto)]
      (if (nil? url)
        (assoc db :current-occupancy (rand))
        db))))


(reg-event-fx
  :set-selected-resto
  validate-spec-mw
  (fn [cofx [_ value]]
    {:db (assoc (:db cofx) :selected-resto value)
     :dispatch [:compute-occupancy value]}))

