(ns dc.macros)

(defn normalize-fn [method-name body map?]
  `(~method-name ~(first body)
                 ~@(drop 1 body)))

(defmacro defcom [name args & body]
  ;; TODO: Handle open-ended protocols that user might want to add
  (let [map?       (map? (first body))
        body       (if map?
                     (first body)
                     {:render `([~'_] ~@body)})
        add-method (fn [protocol method-name body]
                     (list protocol (normalize-fn method-name body map?)))
        dname      (if-let [display-name (:display-name (meta name))]
                     `([~'_] ~display-name)
                     `([~'_] ~(str name))
                     )
        methods    (merge {:display-name dname}
                          body)
        lookup     {:display-name ['om/IDisplayName 'display-name]
                    :will-mount   ['om/IWillMount 'will-mount]
                    :did-mount    ['om/IDidMount 'did-mount]
                    :will-unmount ['om/IWillUnmount 'will-mount]
                    :did-unmount  ['om/IDidUnmount 'did-unmount]
                    :render       ['om/IRender 'render []]}]
    `(defn ~name ~args
       (reify
         ~@(mapcat (fn helper [[method-name method-body]]
                     (let [[pname mname] (get lookup method-name)]
                       (add-method pname mname method-body))) methods)))))

(comment
  ;; defcom form would supprt three increasing levels of specificity

  ;; Level 1, no metadata, body is a list, assumed to be IRender
  (defcom slider [payload owner opts]
    (html/html [:div "whatever"]))

  (defn slider [payload owner opts]
    (reify
      om/IRender
      (render [_]
        (html/html [:div "whatever"]))))

  ;; Level 2, metadata, body is a list, assumed to be IRender
  (defcom ^{:name "SliderCom2"
            :doc "What what"}
    slider-2 [payload owner opts]
    (html/html [:div "whatever"]))

  (defn slider-2 [payload owner opts]
    (reify
      om/IDisplayName
      (display-name [_] "SliderCom2")
      om/IRender
      (render [_]
        (html/html [:div "whatever"]))))

  ;; Level 3, metadata (optional), body is a hashmap with specific
  ;; lifecycle methods
  (defcom ^{:doc "What what"}
    slider-3 [payload owner opts]
    {:display-name ([_] "SliderCom3")
     :will-mount   ([_] (om/set-state! owner :mounting? true))
     :did-mount    ([_] (om/set-state! owner :mounting? false))
     :render       ([_] (html/html [:div "whatever"]))})

  (defn slider-3 [payload owner opts]
    (reify
      om/IDisplayName
      (display-name [_] "SliderCom3")
      om/IWillMount
      (will-mount [_]
        (om/set-state! owner :mounting? true))
      om/IDidMount
      (did-mount [_]
        (om/set-state! owner :mounting? false))
      om/IRender
      (render [_]
        (html/html [:div "whatever"])))))
