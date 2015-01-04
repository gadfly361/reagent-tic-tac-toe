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

;; NOTE: The following functions will have multiple arity. The first will work on atoms which are used during actual gameplay in the UI. However the second will work on a snapshot of that atom in the form of a map.  The snapshot will be used to determine the best-next-space using the minimax algorithm.

;; ----------
(defn which-player-turn? 
  ([] (if (@game-state :player1-turn?) :player1 :player2))
  ([snapshot] (if (snapshot :player1-turn?) :player1 :player2)))

(defn change-player-turn 
  ([] (put! :player1-turn? (not (@game-state :player1-turn?))))
  ([snapshot] (assoc snapshot :player1-turn? (not (snapshot :player1-turn?)))))

;; ----------
(defn player-spaces 
  ([player] (@game-state (player-spaces-keyword player)))
  ([snapshot player] (snapshot (player-spaces-keyword player))))

(defn other-player-spaces 
  ([player] (@game-state (player-spaces-keyword (other-player player))))
  ([snapshot player] (snapshot (player-spaces-keyword (other-player player)))))

(defn take-space 
  ([space] (let [player-spaces-k (player-spaces-keyword (which-player-turn?))]
             (put! player-spaces-k (conj (@game-state player-spaces-k) space))))
  ([snapshot space] (let [player-spaces (player-spaces-keyword (which-player-turn? snapshot))]
                (assoc snapshot player-spaces (conj (snapshot player-spaces) space)))))

(defn all-taken-spaces 
  ([] (union (@game-state :player1-spaces) (@game-state :player2-spaces)))
  ([snapshot] (union (snapshot :player1-spaces) (snapshot :player2-spaces))))

(defn all-remaining-spaces 
  ([] (difference (into (sorted-set) (board-spaces (@game-state :board-size)))
                  (all-taken-spaces)))
  ([snapshot] (difference (into (sorted-set) (board-spaces (snapshot :board-size)))
                    (all-taken-spaces snapshot))))

(defn space-available? 
  ([space] (if (space (all-remaining-spaces)) true false))
  ([snapshot space] (if (space (all-remaining-spaces snapshot)) true false)))

;; ----------
(defn player-win? 
  ([player] (some #(every? (@game-state (player-spaces-keyword player)) %)
                  (board-wins (@game-state :board-size))))
  ([snapshot player] (some #(every? (snapshot (player-spaces-keyword player)) %)
                           (board-wins (snapshot :board-size)))))

(defn cats-game? 
  ([] (and (empty? (all-remaining-spaces)) 
           (not (player-win? :player1))
           (not (player-win? :player2))))
  ([snapshot] (and (empty? (all-remaining-spaces snapshot)) 
                   (not (player-win? snapshot :player1))
                   (not (player-win? snapshot :player2)))))

(defn game-over? 
  ([] (or (cats-game?) (player-win? :player1) (player-win? :player2)))
  ([snapshot] (or (cats-game? snapshot) (player-win? snapshot :player1) (player-win? snapshot :player2))))

;; ----------
(defn player-turn-sequence 
  ([space] (when (and (space-available? space) 
                      (not (game-over?)))
             (take-space space)
             (change-player-turn))))

(def player-turn-sequence-memo
  (memoize (fn [snapshot space] 
             (when (and (space-available? snapshot space) 
                        (not (game-over? snapshot)))
               (->> (take-space snapshot space)
                    (change-player-turn))))))
