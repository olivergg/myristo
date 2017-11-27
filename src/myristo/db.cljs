(ns myristo.db
  (:require [clojure.spec.alpha :as s]))

;; spec of app-db
(s/def ::greeting string?)
(s/def ::app-db
  (s/keys :req-un [::greeting]))


;; initial state of app-db
(def app-db {:greeting "Ristorante watcher"

             :restaurants {:marcopolo {:label "Marco polo" :url "https://sodexo-riemarcopolo.moneweb.fr"}
                           :random1 {:label "Random 1"}
                           :random2 {:label "Random 2"}
                           :steria  {:label "Steria" :url "http://sodexo-steria.moneweb.fr"}}

             :selected-resto :random1
             :current-occupancy 0.0
             :available-seats 0
             :total-seats 100})

