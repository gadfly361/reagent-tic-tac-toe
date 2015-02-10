(ns t3.ui.api
  (:require [t3.game.board :as board]
            [t3.game.engine :as engine :refer [game-state playing-against-state]]
            [t3.game.ai :as ai]))

;; ----------
(defn board-size []
  (@game-state :board-size))

(defn set-board-size [n]
  (engine/put! :board-size n))

(defn board-spaces
  "This will return a all the board spaces"
  []
  (board/board-spaces (board-size)))

(defn board-rows 
  "This will return a list of board rows"
  []
  (board/board-rows (board-size)))

;; ----------
(defn player-spaces 
  "Spaces owned by player"
  [player]
  (engine/player-spaces player))

(defn which-player-turn? 
  "This will return Player1 or Player2 as a string, depending on
  whose turn it is."
  []
  (engine/which-player-turn?))

;; ----------
(defn player-win?
  "This will return true if player won."
  [player]
  (engine/player-win? player))

(defn cats-game?
  "This will return true if cat's game."
  []
  (engine/cats-game?))

(defn game-over?
  "This will return true if game is over"
  []
  (engine/game-over?))

;; ----------
(defn reset-game 
  "This will reset the game"
  []
  (engine/reset-game))

(defn toggle-playing-against
  "This will toggle between playing against a computer (true)
  and against a human (false)"
  []
  (engine/toggle-playing-against))

(defn playing-against-computer?
  "This will return who you are playing against. 
  Computer (true), human (false)."
  []
  (@playing-against-state :playing-against-computer?))

;; ----------
(defn click-space
  "Allows player to take space on mouse click"
  [space]
  (if (playing-against-computer?)

    (if (engine/player-turn-sequence space)
        (do (engine/player-turn-sequence space)
            (if (ai/best-next-space)
                (engine/player-turn-sequence (ai/best-next-space)))))
    
    (engine/player-turn-sequence space)))
