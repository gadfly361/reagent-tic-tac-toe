(ns tic-tac-toe.logic
  (:require [clojure.set :refer [union difference intersection]]
            [tic-tac-toe.session :refer [get-state get-in-state put! put-in!]]
            [tic-tac-toe.board :as board]))

;; Tells you whose turn it is.
(defn turn []
  (if (get-state :player1-turn?) :player1 :player2))

;; Convenience function to tell you whose turn it isn't.
(defn not-turn []
  (if (get-state :player1-turn?) :player2 :player1))

;; If no one owns the space, it can be taken.
(defn can-space-be-taken? [s]
  (not (or ((get-in-state [:player1 :spaces]) s)
           ((get-in-state [:player2 :spaces]) s))))

;; This checks to see if a player has won the game.
(defn did-player-win? [s]
  (or (get-in-state [:player1 :win?])
      (get-in-state [:player2 :win?])))

;; This keeps tabs on which spaces are remaining.
(defn remaining-spaces []
  (difference board/spaces (get-in-state [:player1 :spaces]) (get-in-state [:player2 :spaces])))

;; This is a way to determine whether or not players should be allowed to continue picking spaces.
(defn should-game-continue? [s]
  (and (can-space-be-taken? s)
       (remaining-spaces)
       (not (did-player-win? s))))

;; This updates the application state when someone takes a space.
(defn take-space [space]
  (let [loc [(turn) :spaces]]
    (put-in! loc (conj (get-in-state loc) space))))

;; This updates the application state regarding whether or not a player has won.
(defn update-win [space]
  (put-in! [(turn) :win?] (some #(every? (get-in-state [(turn) :spaces]) %) board/win-scenarios)))

;; Once a player's turn is over, this changes the *player1-turn?* boolean which is used to determine whose turn it is.
(defn change-turn [space]
  (put! :player1-turn? (not (get-state :player1-turn?))))

;; Win scenarioes for a given player
(defn ws-for-player [player]
  (let [p (if (= :player1 player) :player2 :player1)]
    (->> (for [ws board/win-scenarios]
           (empty? (intersection ws (get-in-state [p :spaces]))))
      (map #(if %2 %1) board/win-scenarios)
      (remove nil?))))

;; This is a way to identify which spaces we should care about for a given player.
(defn spaces-of-interest [player]
  (->> (ws-for-player player)
    (apply union)
    (intersection (remaining-spaces))))

;; This ranks the spaces of interest for a given player.
(defn rank-spaces-of-interest [player]
  (->> (map #(intersection (spaces-of-interest player) %1) (ws-for-player player))
    (sort-by count)))

;; This provides some logic for how the computer should pick its next space.
(defn computer-best-space []
  (cond 
    ;; If computer can win on next move, do that
    (= 1 (count (first (rank-spaces-of-interest (get-in-state [(turn) :spaces])))))
    (first (first (rank-spaces-of-interest (get-in-state [(turn) :spaces]))))
    ;; If space5 is available, take that
    (:space5 (remaining-spaces)) :space5
    ;; If player1 picked space5 on first move, take space1
    (= (sorted-set :space5) (get-in-state [(not-turn) :spaces])) :space1
    ;; If player1 picked space5 on first move, and then space9, take space7
    (= (sorted-set :space5 :space9) (get-in-state [(not-turn) :spaces])) :space7
    ;; Otherwise, take the best move from player1
    :else (first (first (rank-spaces-of-interest (not-turn))))))

(defn pick-space [s]
  (doto s
    (take-space)
    (update-win)
    (change-turn)))

;; There are two click-space functions, since a player can play against a human or a computer
(defn click-space-against-human [s]
  (when (should-game-continue? s)
    (pick-space s)))

(defn click-space-against-computer [s]
  (if (= 1 (count (remaining-spaces)))
    ;; 
    (when (should-game-continue? s)
      (pick-space s))
    ;; 
    (when (should-game-continue? s)
      (pick-space s)
      (let [cbs (computer-best-space)]
        (when (should-game-continue? cbs)
          (pick-space cbs))))))
