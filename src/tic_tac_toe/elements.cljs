(ns tic-tac-toe.elements
  (:require [tic-tac-toe.session :as session :refer [get-state get-in-state local-put!]]
            [tic-tac-toe.logic :as logic]
            [clojure.string :refer [capitalize]]))

;; This is an attempt to resize the game board on different screens.
(defn bootstrap-cols [n]
  (str "col-sm-" n " "
       "col-md-" n " "
       "col-lg-" n " "))

;; Displays space of tic tac toe board. The space will change color depending on which
;; player clicks on the space.  Also, the space is aware of whether or not you are playing
;; against a human or against the computer.
(defn space [n local-state]
  (let [s (keyword (str "space" n))
        owner (cond (s (get-in-state [:player1 :spaces])) :player1
                    (s (get-in-state [:player2 :spaces])) :player2
                    :else :default)
        image (str (name owner))]
    [:img {:src (str "img/" image ".png")
           :style {:width "30%"}
           :on-click #(if (@local-state :against-computer?) 
                        (logic/click-space-against-computer s)
                        (logic/click-space-against-human s))}]))

;; This will reset the global application state. However, this will not reset
;; if the player is playing against a human or the computer.
(defn restart []
  (when-not (= 9 (count (logic/remaining-spaces)))
    [:div.btn.btn-primary {:on-click #(reset! session/app-state session/init-game)} [:h4 "Restart"]]))

;; This will togget whether or not the player is playing against a human or the computer.
(defn toggle-against [local-state]
  (when (= 9 (count (logic/remaining-spaces)))
    [:div.btn.btn-success {:on-click #(local-put! local-state :against-computer? (if (@local-state :against-computer?) false true))} 
     [:h4 (if (@local-state :against-computer?) "Play against human" "Play against computer")]]))


(defn did-player1-win? []
  (get-in-state [:player1 :win?]))

(defn did-player2-win? []
  (get-in-state [:player2 :win?]))

(defn cats-game? []
  (and (empty? (logic/remaining-spaces)) (not (did-player1-win?)) (not (did-player2-win?))))

(defn game-ended? []
  (or (did-player1-win?)
      (did-player2-win?)
      (cats-game?)))

;; Puts commas inbetween elements of a list
(defn display-list [lst]
  (interpose ", " (map name lst)))

;; Shows a pointer next to the player whose turn it is.
;; This is only on if the player is playing against a human (see it being used in `player-panel`).
(defn turn-icon [player]
  (when-not (game-ended?)
    (if (= player (logic/turn)) [:i.fa.fa-long-arrow-left])))

;; A star will appear next to the winning player.
(defn win-icon [player]
  (when (get-in-state [player :win?]) [:i.fa.fa-star]))

(defn player-panel [player panel-type local-state]
  [:div.col-md-6
   [(keyword (str "div.panel.panel-" panel-type))
    ;; heading
    [:div.panel-heading [:h4 (capitalize (str (name player) " "))
                         (if (@local-state :against-computer?) 
                           (str (if (= :player1 player) "(You) " "(Computer) "))
                           [turn-icon player])
                         [win-icon player] ]]
    ;; body
    [:div.panel-body
     [:div "Spaces: " (display-list (get-in-state [player :spaces]))]] ]])

;; If a player wins, then this notification will appear
(defn win-notification [player alert-type text]
  (when (get-in-state [player :win?]) [(keyword (str "div.alert.alert-" alert-type)) text]))

;; If it's a cat's game, then this notification will appear.
(defn cats-game-notification []
  (when (cats-game?)
    [:div.alert.alert-danger "Cat's game!"]))

