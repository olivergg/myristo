(ns myristo.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :get-greeting
 (fn [db _]
   (:greeting db)))

(reg-sub
  :get-restaurants
  (fn [db _]
    (:restaurants db)))


(reg-sub
  :get-selected-resto
  (fn [db _]
    (:selected-resto db)))


(reg-sub
  :get-occupancy
  (fn [db _]
    (:current-occupancy db)))

(reg-sub
  :get-db
  (fn [db _]
    db))