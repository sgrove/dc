# dc

An experiment in reduced-boilerplate syntax for Om components, ideally without compromises.

## Overview

This library just introduces a single macro, `defcom`. It has some nice sugar, and can be used in three (increasingly specific) ways:

``` clj
;; Level 1, no metadata, body is a list, assumed to be IRender
(defcom slider [payload owner opts]
  (html/html [:div "whatever"]))
```
...expands to:
``` clj
(defn slider [payload owner opts]
  (reify
    om/IDisplayName (display-name [_] "slider")
    om/IRender
    (render [_]
      (html/html [:div "whatever"]))))
```

You can also specify an explicit display-name in the meta-data with this form
``` clj
;; Level 2, metadata, body is a list, assumed to be IRender
(defcom ^{:name "SliderCom2"
          :doc "What what"}
  slider-2 [payload owner opts]
  (html/html [:div "whatever"]))
```
...expands to:
```clj
(defn slider-2 [payload owner opts]
  (reify
    om/IDisplayName
    (display-name [_] "SliderCom2")
    om/IRender
    (render [_]
      (html/html [:div "whatever"]))))
```

If you need to use other lifecycle methods in your component, use the hashmap syntax:

```clj
;; Level 3, metadata (optional), body is a hashmap with specific
;; lifecycle methods
(defcom slider-3 [payload owner opts]
  {:display-name ([_] "SliderCom3")
   :will-mount   ([_] (om/set-state! owner :mounting? true))
   :did-mount    ([_] (om/set-state! owner :mounting? false))
   :render       ([_] (html/html [:div "whatever"]))})
```

...expands to:
```clj
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
      (html/html [:div "whatever"]))))
```
    
## Setup

First-time Clojurescript developers, add the following to your bash .profile:

    git clone https://github.com/sgrove/dc.git
    lein cljsbuild auto
    
in another terminal

    python -m SimpleHTTPServer    

## License

Copyright © 2014 Sean Grove

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
