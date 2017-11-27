(ns myristo.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [myristo.handlers]
            [myristo.subs]
            [cljs.pprint :as pprint]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def picker (r/adapt-react-class (.-Picker ReactNative)))
(def pickeritem (r/adapt-react-class (.-Picker.Item ReactNative)))



(def Alert (.-Alert ReactNative))

(defn alert [title]
  (.alert Alert title))

;(defn app-root []
;  (let [greeting (subscribe [:get-greeting])]
;    (fn []
;      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
;       [image {:source (js/require "./assets/images/cljs.png")
;               :style {:width 200
;                       :height 200}}]
;       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
;       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
;                             :on-press #(alert "HELLO!")}
;        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]])))


(defn app-root []
  (let [greeting (subscribe [:get-greeting])
        restaurants (subscribe [:get-restaurants])
        selectedresto (subscribe [:get-selected-resto])
        occupancy (subscribe [:get-occupancy])]
    (fn []
      [view {:style {:flex 1
                     :justify-content "center"
                     :align-items "center"
                     :background-color "white"
                     :padding 40}}

       ;; greeting
       [text {:style {:font-size 30 :color "green" :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
       ;; a logo
       ;[image {:source logo-img
       ;        :style  {:width 80 :height 80 :margin-bottom 30}}]
       ;; some text before the picker
       [text {:style {:color "green" :font-size 24}} "Select a restaurant :"]

       [picker
        {:onValueChange #(dispatch [:set-selected-resto (keyword %1)])
         :selectedValue @selectedresto
         :style { :alignSelf "stretch"}}
        (for [r @restaurants]
          ^{:key (key r)} [pickeritem {:label (:label (val r)) :value (key r)}])]


       ;; a button to retrieve the latest occupancy rate for the selected restaurant
       [touchable-highlight {:style {:background-color "green" :alignSelf "stretch" :padding 10 :border-radius 5}
                             :on-press #(dispatch [:compute-occupancy @selectedresto])}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Get"]]


       ;; a percent bar
       [view {:style {:flex-direction "row" :margin-top 20 :alignSelf "stretch"
                      :height 50  :borderWidth 1 :borderColor "grey" :borderStyle "solid"}}
        [view {:style {:zIndex 1 :padding 10

                       ;;:borderWidth 1 :borderColor "grey" :borderStyle "solid"
                       :position "absolute" :height 48 :width 300}}

         [view {:style {;;:borderWidth 1 :borderColor "green" :borderStyle "solid"
                        :flex 1 :justifyContent "center" :align-items "center"}}
          [text (pprint/cl-format nil "~d %" (int (* 100 @occupancy)))]]]
        [view {:style {  :backgroundColor "mediumseagreen" :flex @occupancy}}]
        [view {:style {  :backgroundColor "beige" :flex (- 1 @occupancy)}}]]

       [text (str "You have selected " (-> @restaurants (@selectedresto) :label))]])))


(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "myristo" #(r/reactify-component app-root)))
