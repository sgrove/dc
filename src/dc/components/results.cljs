(ns dc.components.results ;; this is a path
  (:require [om.core :as om]
            [clojure.string :as string]
            [ankha.core :as ankha]
            [sablono.core :as html :refer-macros [html]])
  (:require-macros [dc.macros :as m]))

(defn strip-filename [grep-line]
  (when-let [filtered-line (re-find #"[-:].*" grep-line)]
    (subs filtered-line 1)))

(m/defcom result-line [payload owner opts]
  (html [:div (strip-filename payload)]))

;; Maybe we should remove the [_] boilerplate? It's consistent with
;; protocol implementations though, so it may be worth the extra
;; typing for a lower cognitive special-syntax burden
(m/defcom ^{:display-name "ResultItem"}
  result-item [payload owner opts]
  {:will-mount ([_] (om/set-state! owner :open? true))
   :render     ([_]
                  (let [lines (->> (:results payload)
                                   string/split-lines
                                   (remove string/blank?)
                                   (take 6))]
                    (html [:div
                           [:div {:style {:background-color "orange"}
                                  :on-click (fn [event]
                                              (.preventDefault event)
                                              (om/update-state! owner :open? not))}
                            "Query:" (get-in payload [:request :query])]
                           (when (om/get-state owner :open?)
                             [:div "Result:" (om/build-all result-line lines)])])))})

(m/defcom result-list [payload owner opts]
  (html [:div (om/build-all result-item payload)]))
