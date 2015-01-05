(ns t3.game.ai
  (:require [clojure.set :refer [intersection union]]
            [t3.game.board :as board]
            [t3.game.engine :refer [game-state 
                                    all-remaining-spaces all-remaining-spaces-memo
                                    which-player-turn? player-win?-memo
                                    game-over? player-turn-sequence-memo
                                    other-player other-player-spaces]]))

;; ----------------------------------------
;; Minimax AI

(defn score [snapshot depth]
  (let [player (which-player-turn? snapshot)
        opponent (other-player player)]
    (cond (player-win?-memo snapshot player) (- 10 depth)
          (player-win?-memo snapshot opponent) (- depth 10)
          :else 0)))

(defn find-best-move [coll]
  (reduce (fn [x y]
            (if (> (first x) (first y))
              x y)) coll))

(defn minimax [snapshot depth]
  (if (game-over? snapshot)
    [(score snapshot depth) "dummy-space"]
    (find-best-move
     (for [space (all-remaining-spaces-memo snapshot)]
       (let [gs (player-turn-sequence-memo snapshot space)
             [space-score best-space] (minimax gs (inc depth))]
         [(* -1 space-score) space] )))))

;; ----------------------------------------
;; Human Computer AI

(defn board-size []
  (@game-state :board-size))

;; ----------
(defn player-win-scenarios 
  "Returns a list of remaining win scenarios. Win scenarios are sets and they include already owned spaces."
  [player]
  (let [win-scenarios (board/board-wins (board-size))]
    (->> (for [ws win-scenarios]
           (empty? (intersection ws (other-player-spaces player))))
         (map #(if %2 %1) win-scenarios)
         (remove nil?))))

(defn player-win-scenarios-sorted 
  "Returns a list of win scenarios that are sorted by how many spaces remain.
  Win scenarios are sets and they do not include already owned spaces  "
  [player]
  (->> (map #(intersection (all-remaining-spaces) %1) 
            (player-win-scenarios player))
       (sort-by count)))

;; ----------
(defn player-win-next-move? 
  "This returns true if the player has a win scenario with only one space unowned 
  (i.e. the player can win next move)"
  [player]
  (= 1 (count (first (player-win-scenarios-sorted player)))))

;; ----------
(defn player-win-scenarios-with-two-left 
  "Returns the win scenarios that are two moves from winning. Win scenarios
  are sets and they do not include already ownded spaces"
  [player]
  (filter #(= 2 (count %)) (player-win-scenarios-sorted player)))

(defn spaces-with-two-left 
  "Returns a set of spaces that are two moves away from winning the game for a player"
  [player]
  (->> (player-win-scenarios-with-two-left player)
       (apply union #{})))

(defn spaces-both-players-two-left 
  "Returns a set of spaces that are two moves away from winning for both players"
  []
  (intersection (spaces-with-two-left :player1) (spaces-with-two-left :player2)))

;; ----------
(defn middle-space []
  (if (= (mod (board-size) 2) 1) 
      (keyword (str "space" (- (/ (* (board-size) (board-size)) 2) 0.5)))))

(defn spaces-two-corners-scenario [player]
  (let [ops (other-player-spaces player)
        bc (board/board-corners (board-size))]
  (if (and (= 2 (count (intersection bc ops)))
           (= 2 (count ops)))
    (->> (map #(disj (all-remaining-spaces) %) bc)
         (apply intersection)))))

;; ----------
(defn rank-spaces-by-occurence 
  "This takes a list of sets (i.e. win scenarios) and ranks 
  spaces by number of occurences in those sets"
  [win-scenarios]
  (->> (mapcat #(into '() %) win-scenarios)
       (sort)
       (partition-by identity)
       (sort-by count)
       (reverse)
       (map first)))

(defn rank-spaces-by-player-win-scenarios [player]
  (rank-spaces-by-occurence (player-win-scenarios-sorted player)))

(defn rank-spaces-both-players-two-left [player]
  (filter #((spaces-both-players-two-left) %)
          (rank-spaces-by-occurence (player-win-scenarios-with-two-left player))))

;; ----------
(defn best-next-space [player]
  (cond 
   ;; Can you win? If so, take winning space.
   (player-win-next-move? player) 
   (first (first (player-win-scenarios-sorted player)))

   ;; ;; Can opponent win? If so, block winning space.
   (player-win-next-move? (other-player player)) 
   (first (first (player-win-scenarios-sorted (other-player player))))

   ;; Take center if available
   ((all-remaining-spaces) (middle-space))
   ((all-remaining-spaces) (middle-space))

   ;; If other player's first two moves is opposite corners, don't take a corner (relevant for 3x3).
   (spaces-two-corners-scenario player)
   (first (spaces-two-corners-scenario player))

   ;; Both players can pick spaces that bring them within two of winning. Pick a space
   ;; that you are both interested in from that set, but rank the spaces from 
   ;; your opponent's point of view.
   (not (empty? (rank-spaces-both-players-two-left (other-player player))))
   (first (rank-spaces-both-players-two-left (other-player player)))

   ;; Both players can pick spaces that bring them within two of winning. Pick a space
   ;; that you are both interested in from that set, but rank from your point of view.
   (not (empty? (rank-spaces-both-players-two-left player)))
   (first (rank-spaces-both-players-two-left player))

   ;; Rank spaces by your possible win scenarios
   (first (rank-spaces-by-player-win-scenarios player))
   (first (rank-spaces-by-player-win-scenarios player))

   ;; Rank spaces by your opponents possible win scenarios. This is in case you can no longer win
   ;; but you want to put up a fight!
   (first (rank-spaces-by-player-win-scenarios (other-player player)))
   (first (rank-spaces-by-player-win-scenarios (other-player player)))

   ;; Take from remaining spaces
   :else (first (all-remaining-spaces))))
