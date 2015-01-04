(ns t3.game.engine
  (:require [clojure.set :refer [union difference]]
            [t3.game.board :refer [board-wins board-spaces]]
            [reagent.core :refer [atom]]))

;; ----------
(def init-game-state
  {:player1-spaces (sorted-set)
   :player2-spaces (sorted-set)
   :player1-turn? true
   :board-size 3})

(def game-state (atom init-game-state))

(defn put! [k v]
  (swap! game-state assoc k v))

(defn reset-game []
  (reset! game-state init-game-state))

(def playing-against-state (atom {:playing-against-computer? true}))

(defn toggle-playing-against []
  (swap! playing-against-state assoc :playing-against-computer? (not (@playing-against-state :playing-against-computer?))))

;; ----------
(defn other-player [player]
  (if (= :player1 player) :player2 :player1))

(defn player-spaces-keyword [player]
  (if (= :player1 player) :player1-spaces :player2-spaces))

;; ----------
(defn which-player-turn? []
  (if (@game-state :player1-turn?) :player1 :player2))

(defn change-player-turn []
  (put! :player1-turn? (not (@game-state :player1-turn?))))

;; ----------
(defn player-spaces [player]
  (@game-state (player-spaces-keyword player)))

(defn other-player-spaces [player]
  (@game-state (player-spaces-keyword (other-player player))))

(defn take-space [space]
  (let [player-spaces-k (player-spaces-keyword (which-player-turn?))]
    (put! player-spaces-k (conj (@game-state player-spaces-k) space))))

(defn all-taken-spaces []
  (union (@game-state :player1-spaces) (@game-state :player2-spaces)))

(defn all-remaining-spaces []
  (difference (into (sorted-set) (board-spaces (@game-state :board-size)))
              (all-taken-spaces)))

(defn space-available? [space]
  (if (space (all-remaining-spaces)) true false))

;; ----------
(defn player-win? [player]
  (some #(every? (@game-state (player-spaces-keyword player)) %)
        (board-wins (@game-state :board-size))))

(defn cats-game? []
  (and (empty? (all-remaining-spaces)) 
       (not (player-win? :player1))
       (not (player-win? :player2))))

(defn game-over? []
  (or (cats-game?) (player-win? :player1) (player-win? :player2)))

;; ----------
(defn player-turn-sequence [space]
  (when (and (space-available? space) 
             (not (game-over?)))
    (take-space space)
    (change-player-turn)))
