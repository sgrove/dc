(defproject dc "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[ankha                        "0.1.4"]
                 [cljsjs/react                 "0.12.2-5"]
                 [ff-om-draggable              "0.0.18"]
                 [garden                       "1.2.5"]
                 [hiccup                       "1.0.5"]
                 [org.clojure/clojure          "1.6.0"]
                 [org.clojure/clojurescript    "0.0-2760"]
                 [org.clojure/core.async       "0.1.346.0-17112a-alpha"]
                 [org.omcljs/om                "0.8.8"]
                 [ring/ring-jetty-adapter      "1.3.2"]
                 [rm-hull/ring-gzip-middleware "0.1.7"]
                 [sablono                      "0.3.1"]]

  :plugins [[lein-cljsbuild "1.0.4"]]

  :source-paths ["src" "target/classes"]

  :clean-targets ["out/dc" "out/dc.js"]

  :cljsbuild {:builds [{:id           "dc"
                        :source-paths ["src"]
                        :compiler     {:output-to      "out/dc.js"
                                       :output-dir     "out"
                                       :optimizations  :none
                                       :cache-analysis true
                                       :source-map     true}}]})
