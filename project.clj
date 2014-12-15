(defproject tic-tac-toe "0.1.0-SNAPSHOT"
  :source-paths ["src" "dev"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2342"]
                 [ring "1.2.2"]
                 [compojure "1.1.6"]
                 [enlive "1.1.5"]
                 ;; ReactJS wrapper
                 [reagent "0.4.2"]
                 ;; Client-side routing
                 [secretary "1.2.0"]]

  :min-lein-version "2.4.3"

  :plugins [;; Emacs repl
            [cider/cider-nrepl "0.8.0-SNAPSHOT"]
            ;; brepl
            [com.cemerick/austin "0.1.4"]
            ;; cljs builder
            [lein-cljsbuild "1.0.3"]
            ;; documentation generator
            [lein-marginalia "0.8.0"]]
  
 :cljsbuild {:builds [{;; Optional name of the build:
                        :id "tic-tac-toe"
                        ;; Directories of interest:
                        :source-paths ["src" "dev"]
                        ;; Compiler flags:
                        :compiler {;; Where to save the file:
                                   :output-to "resources/public/js/app.js"
                                   ;; Where to put the output directory
                                   :output-dir "resources/public/js/out"
                                   ;; Source-maps
                                   :source-map "resources/public/js/app.js.map"
                                   ;; Optimizations:
                                   :optimizations :whitespace}}]})
