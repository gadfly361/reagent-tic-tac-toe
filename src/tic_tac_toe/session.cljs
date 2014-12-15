(ns tic-tac-toe.session
  (:require [reagent.core :as reagent :refer [atom]]))

;; When the browser first loads, or when the reset button is pressed, this is the state of the application.
(def init-game {:player1 {:spaces (sorted-set)}
                :player2 {:spaces (sorted-set)}
                :player1-turn? true})

;; This is a *reagent* atom, which means every time a change is made to 
;; the atom, the render phase of the React.js lifecycle is executed.
(def app-state (atom init-game))

;; Helper functions for deailing with state
(defn get-state [k & [default]]
  (get @app-state k default))

(defn get-in-state [k & [default]]
  (get-in @app-state k default))

(defn put! [k v]
  (swap! app-state assoc k v))

(defn put-in! [k v]
  (swap! app-state assoc-in k v))

(defn local-put! [a k v]
  (swap! a assoc k v))
