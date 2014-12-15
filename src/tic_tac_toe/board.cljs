(ns tic-tac-toe.board)

;; This represents the nine spaces available in a tic tac toe game.
(def spaces
  (sorted-set :space1 :space2 :space3
              :space4 :space5 :space6
              :space7 :space8 :space9))

;; There are 8 different ways to win a game of tic tac toe.
(def win-scenarios
  [;; Horizantal wins
   #{:space1 :space2 :space3}
   #{:space4 :space5 :space6}
   #{:space7 :space8 :space9}
   ;; Vertical wins
   #{:space1 :space4 :space7}
   #{:space2 :space5 :space8}
   #{:space3 :space6 :space9}
   ;; Diagonal wins
   #{:space1 :space5 :space9}
   #{:space7 :space5 :space3} ])

