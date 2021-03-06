(ns t3.game.ai-spec
  (:require-macros [speclj.core :refer [describe it should= before]])
  (:require [speclj.core]
            [t3.game.ai :as ai]
            [t3.game.engine :as engine :refer [game-state]]))

;; ----------------------------------------
;; Minimax AI

(describe "minimax score:"
          (before (engine/reset-game))
          (it "player1 won, so from player2's persective should be -10"
              (should= -10
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0) ;player1
                           (engine/take-space :space1) ;player1
                           (engine/player-turn-sequence :space2) ;player1
                           (ai/score @game-state 0))))   ;player2's turn
          (it "player1 won, so from player1's perspective should be 10"
              (should= 10
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0) ;player1
                           (engine/take-space :space1) ;player1
                           (engine/take-space :space2) ;player1
                           (ai/score @game-state 0))))   ;player1's turn
          (it "game isn't over yet, should be 0"
              (should= 0
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0) ;player1
                           (engine/take-space :space1) ;player1
                           (engine/player-turn-sequence :space3) ;player1
                           (ai/score @game-state 0))))  ;player2's turn
          (it "game isn't over yet, should be 0"
              (should= 0
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space0) ;player1
                           (engine/player-turn-sequence :space1) ;player2
                           (engine/player-turn-sequence :space8) ;player1
                           (engine/player-turn-sequence :space2) ;player2
                           (engine/player-turn-sequence :space7) ;player1
                           (ai/score @game-state 0)))) ;player2's turn
          (it "player1 won, so from player2's perspective should be -10"
              (should= -10
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space0) ;player1
                           (engine/player-turn-sequence :space1) ;player2
                           (engine/player-turn-sequence :space8) ;player1
                           (engine/player-turn-sequence :space2) ;player2
                           (engine/player-turn-sequence :space4) ;player1
                           (ai/score @game-state 0))))) ;player2's turn

(describe "minimax:"
          (before (engine/reset-game))
          (it "should try to win by picking :space4"
              ;; Because the next move would be a win, the depth is 1, which makes the score a 9 instead of a 10.
              (should= [9 :space4]
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space0) ;player1
                           (engine/player-turn-sequence :space1) ;player2
                           (engine/player-turn-sequence :space8) ;player1
                           (engine/player-turn-sequence :space2) ;player2
                           (ai/minimax @game-state 0))))
          ;; ----------
          (it "should try to block by picking :space1"
              (should= [0 :space1]
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space4) ;player1
                           (engine/player-turn-sequence :space0) ;player2
                           (engine/player-turn-sequence :space8) ;player1
                           (engine/player-turn-sequence :space2) ;player2
                           (ai/minimax @game-state 0))))
          ;; ----------
          (it "should try to block by picking :space6"
              ;; Note: the score is 7 because player1 will eventually win after 3 plays.
              (should= [7 :space6]
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space4) ;player1
                           (engine/player-turn-sequence :space0) ;player2
                           (engine/player-turn-sequence :space8) ;player1
                           (engine/player-turn-sequence :space3) ;player2
                           (ai/minimax @game-state 0))))
          (it "should try to create a fork by picking :space8 (as well as foiling opponent's attempt to set up a fork."
              ;; Note: the score is 7 because player1 will eventually win after 3 plays.
              (should= [7 :space8]
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space4) ;player1
                           (engine/player-turn-sequence :space1) ;player2
                           (engine/player-turn-sequence :space7) ;player1
                           (engine/player-turn-sequence :space5) ;player2
                           (ai/minimax @game-state 0))))
          (it "should try to create a fork by picking :space6 (as well as foiling opponent's attempt to set up a fork."
              ;; Note: the score is 7 because player1 will eventually win after 3 plays.
              (should= [7 :space6]
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space4) ;player1
                           (engine/player-turn-sequence :space1) ;player2
                           (engine/player-turn-sequence :space3) ;player1
                           (engine/player-turn-sequence :space5) ;player2
                           (ai/minimax @game-state 0))))
          (it "should win by picking :space2"
              ;; Note: the score is 9 because player1 will win after 1 play.
              (should= [9 :space2]
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space4) ;player1
                           (engine/player-turn-sequence :space1) ;player2
                           (engine/player-turn-sequence :space6) ;player1
                           (engine/player-turn-sequence :space5) ;player2
                           (ai/minimax @game-state 0)))))

;; ----------------------------------------
;; Human Computer AI

(describe "player-win-scenarios:"
          (before (engine/reset-game))
          (it "should have 8 win scenarios"
              (should= '(#{:space0 :space1 :space2}
                         #{:space3 :space4 :space5}
                         #{:space6 :space7 :space8}
                         #{:space0 :space3 :space6}
                         #{:space1 :space4 :space7}
                         #{:space2 :space5 :space8}
                         #{:space0 :space4 :space8}
                         #{:space2 :space4 :space6})
                       (do (engine/put! :board-size 3)
                           (ai/player-win-scenarios :player1)
                           )))
          (it "player1 should have 5 win scenarios"
              (should= '(;; #{:space0 :space1 :space2}
                         #{:space3 :space4 :space5}
                         #{:space6 :space7 :space8}
                         #{:space0 :space3 :space6}
                         #{:space1 :space4 :space7}
                         ;; #{:space2 :space5 :space8}
                         #{:space0 :space4 :space8}
                         ;; #{:space2 :space4 :space6}
                         )
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space1) ;player1
                           (engine/player-turn-sequence :space2) ;player2
                           (engine/player-turn-sequence :space3) ;player1
                           (ai/player-win-scenarios :player1)
                           )))
          (it "player2 should have 4 win scenarios"
              (should= '(;; #{:space0 :space1 :space2}
                         ;; #{:space3 :space4 :space5}
                         #{:space6 :space7 :space8}
                         ;; #{:space0 :space3 :space6}
                         ;; #{:space1 :space4 :space7}
                         #{:space2 :space5 :space8}
                         #{:space0 :space4 :space8}
                         #{:space2 :space4 :space6}
                         )
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space1) ;player1
                           (engine/player-turn-sequence :space2) ;player2
                           (engine/player-turn-sequence :space3) ;player1
                           (ai/player-win-scenarios :player2)))))

(describe "player-win-next-move?:"
          (before (engine/reset-game))
          (it "player1 should be able to win on next move"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (ai/player-win-next-move? :player1))))
          (it "player1 should not be able to win on next move"
              (should= false
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (ai/player-win-next-move? :player1)))))

(describe "human-computer-ai:"
          (before (engine/reset-game))
          (it "3x3 first move, player1 should pick middle"
              (should= :space4
                       (do (engine/put! :board-size 3)
                           (ai/human-computer-ai :player1))))
          (it "3x3 player1 picked :space4, player2 should a pick corner"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space4)
                           (if (#{:space0 :space2 :space6 :space8} (ai/human-computer-ai :player2)) true false))))
          (it "3x3 player1 picked :space0 and :space1, player1 should a pick space:6 to win"
              (should= :space6
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (engine/take-space :space3)
                           (ai/human-computer-ai :player1))))
          (it "3x3 player1 picked :space1 and :space4, player2 should a pick space:7 to block"
              (should= :space7
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space1)
                           (engine/take-space :space4)
                           (ai/human-computer-ai :player2))))
          (it "3x3 player1 picked :space0 and :space4, player2 should a pick opposite corner :space 8"
              (should= :space8
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (engine/take-space :space4)
                           (ai/human-computer-ai :player2))))
          (it "3x3 player1 picked :space2 and :space4, player2 should a pick opposite corner :space 6"
              (should= :space6
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space2)
                           (engine/take-space :space4)
                           (ai/human-computer-ai :player2))))
          (it "3x3 player1 picked :space0 and :space8, player2 should a pick something"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space0)
                           (engine/player-turn-sequence :space4)
                           (engine/player-turn-sequence :space8)
                           (if ((engine/all-remaining-spaces) (ai/human-computer-ai :player2)) true false))))
          (it "3x3 player1 picked :space0, :space3, :space2, and :space7, player2 should a pick something"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space0)
                           (engine/player-turn-sequence :space4)
                           (engine/player-turn-sequence :space3)
                           (engine/player-turn-sequence :space6)
                           (engine/player-turn-sequence :space2) 
                           (engine/player-turn-sequence :space1)
                           (engine/take-space :space7)
                           (if ((engine/all-remaining-spaces) (ai/human-computer-ai :player2)) true false))))
          ;; Checking clever ways to win
          (it "3x3 player1 picked :space4, :space0, player2 should a pick :space6"
              (should= :space6
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space4)
                           (engine/player-turn-sequence :space8)
                           (engine/player-turn-sequence :space0)
                           (ai/human-computer-ai :player2))))
          (it "3x3 player1 picked :space1, :space4, player2 should a pick :space2"
              (should= :space2
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space1)
                           (engine/player-turn-sequence :space4)
                           (engine/player-turn-sequence :space5)
                           (ai/human-computer-ai :player2))))
          (it "3x3 player1 picked :space1, :space4, player2 should a pick anything but a corner"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space8)
                           (engine/player-turn-sequence :space4)
                           (engine/player-turn-sequence :space0)
                           (if (#{:space1 :space3 :space5 :space7} (ai/human-computer-ai :player2)) true)))))
