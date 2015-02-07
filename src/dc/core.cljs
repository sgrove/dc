(ns dc.core
  (:require [ankha.core :as ankha]
            [clojure.string :as string]
            [dc.components.results :as c-result]
            [ff-om-draggable.core :as ff]
            [om.core :as om]
            [om.dom :as dom]
            [sablono.core :as html :refer-macros [html]])
  (:require-macros [dc.macros :as m]))

(enable-console-print!)

(def firebase-app-url
  "https://hackerscool.firebaseio.com/")

(def r
  (js/Firebase. (str firebase-app-url "grep/search/response")))

(def app-state (atom {:docs []
                      :text "Hello world!"
                      :example {:greeting "Aloha" :count 0}
                      :position {:left 500 :top 20}}))

(m/defcom example [payload]
  (html [:div
         [:p (:greeting payload)]
         [:button {:on-click (fn [event]
                               (om/transact! payload :count inc))}
          (:count payload)]]))

(m/defcom root [app]
  (let [docs        (:docs app)
        sorted-docs (->> docs
                         (sort-by :time)
                         reverse)]
    (html [:div
           [:h1 (:text app)]
           (om/build example (:example app))
           (om/build c-result/result-list sorted-docs)
           [:div
            (om/build (ff/draggable-item ankha/inspector [:position]) app)]])))

(defn -main []
  (.on r "child_added" (fn add-child! [data]
                         (let [blob (merge {:key (.key data)}
                                           (js->clj (.val data) :keywordize-keys true))]
                           (swap! app-state update-in [:docs] conj blob))))
  (.on r "child_removed" (fn remove-child! [data]
                           (let [blob (js->clj (.val data) :keywordize-keys true)]
                             (swap! app-state update-in [:docs] (fn [docs] (vec (remove #(= (.key data) (:key %)) docs)))))))
  (om/root
   root
   app-state
   {:target (. js/document (getElementById "app"))}))

(-main)
