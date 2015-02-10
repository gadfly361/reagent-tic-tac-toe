(ns t3.game.engine-spec
  (:require-macros [speclj.core :refer [describe it should= before]])
  (:require [speclj.core]
            [t3.game.engine :as engine :refer [game-state]]))

(describe "which-player-turn? and change-player-turn:"
          (before (engine/reset-game))
          ;; -----
          (it "should start game as player1's turn"
              (should= :player1
                       (engine/which-player-turn?)))
          (it "snapshot: should start game as player1's turn"
              (should= :player1
                       (engine/which-player-turn? @game-state)))
          ;; -----
          (it "should be player2's turn after player1 goes"
              (should= :player2
                       (do (engine/change-player-turn)
                           (engine/which-player-turn?))))
          (it "snapshot: should be player2's turn after player1 goes"
              (should= :player2
                       (do (engine/change-player-turn)
                           (engine/which-player-turn? @game-state))))
          ;; -----
          (it "should be player1's turn again after player1 and player2 goes"
              (should= :player1
                       (do (engine/change-player-turn)
                           (engine/change-player-turn)
                           (engine/which-player-turn?))))
          (it "snapshot: should be player1's turn again after player1 and player2 goes"
              (should= :player1
                       (do (engine/change-player-turn)
                           (engine/change-player-turn)
                           (engine/which-player-turn? @game-state)))))

(describe "player-spaces and take-space:"
          (before (engine/reset-game))
          ;; -----
          (it "player1 should take :space1"
              (should= (sorted-set :space1)
                       (do (engine/take-space :space1)
                           (engine/player-spaces :player1))))
          (it "snapshot: player1 should take :space1"
              (should= (sorted-set :space1)
                       (do (engine/take-space :space1)
                           (engine/player-spaces @game-state :player1))))
          ;; -----
          (it "player2 should take :space2"
              (should= (sorted-set :space2)
                       (do (engine/change-player-turn)
                           (engine/take-space :space2)
                           (engine/player-spaces :player2))))
          (it "snapshot: player2 should take :space2"
              (should= (sorted-set :space2)
                       (do (engine/change-player-turn)
                           (engine/take-space :space2)
                           (engine/player-spaces @game-state :player2))))
          ;; -----
          (it "player1 should take :space5 & :space7, player2 should take :space6"
              (should= (list (sorted-set :space5 :space7) (sorted-set :space6))
                       (do (engine/take-space :space5)
                           (engine/change-player-turn)
                           (engine/take-space :space6)
                           (engine/change-player-turn)
                           (engine/take-space :space7)
                           (list (engine/player-spaces :player1)
                                 (engine/player-spaces :player2)))))
          (it "player1 should take :space5 & :space7, player2 should take :space6"
              (should= (list (sorted-set :space5 :space7) (sorted-set :space6))
                       (do (engine/take-space :space5)
                           (engine/change-player-turn)
                           (engine/take-space :space6)
                           (engine/change-player-turn)
                           (engine/take-space :space7)
                           (list (engine/player-spaces @game-state :player1)
                                 (engine/player-spaces @game-state :player2))))))

(describe "all-taken-spaces:"
          (before (engine/reset-game))
          (it "player1 and player2 should collectively have taken :space5, :space6, and :space7"
              (should= (sorted-set :space5 :space6 :space7)
                       (do (engine/take-space :space5)
                           (engine/change-player-turn)
                           (engine/take-space :space6)
                           (engine/change-player-turn)
                           (engine/take-space :space7)
                           (engine/all-taken-spaces))))
          (it "snapshot: player1 and player2 should collectively have taken :space5, :space6, and :space7"
              (should= (sorted-set :space5 :space6 :space7)
                       (do (engine/take-space :space5)
                           (engine/change-player-turn)
                           (engine/take-space :space6)
                           (engine/change-player-turn)
                           (engine/take-space :space7)
                           (engine/all-taken-spaces @game-state)))))

(describe "all-remaining-spaces:"
          (before (engine/reset-game))
          ;; -----
          (it "should have 5 spaces left"
              (should= #{:space4 :space5 :space6 :space7 :space8}
                       (do (engine/take-space :space0)
                           (engine/change-player-turn)
                           (engine/take-space :space1)
                           (engine/change-player-turn)
                           (engine/take-space :space2)
                           (engine/change-player-turn)
                           (engine/take-space :space3)
                           (engine/all-remaining-spaces))))
          (it "snapshot: should have 5 spaces left"
              (should= #{:space4 :space5 :space6 :space7 :space8}
                       (do (engine/take-space :space0)
                           (engine/change-player-turn)
                           (engine/take-space :space1)
                           (engine/change-player-turn)
                           (engine/take-space :space2)
                           (engine/change-player-turn)
                           (engine/take-space :space3)
                           (engine/all-remaining-spaces-memo @game-state))))
          ;; -----
          (it "should have 0 spaces left"
              (should= #{}
                       (do (engine/take-space :space0)
                           (engine/change-player-turn)
                           (engine/take-space :space1)
                           (engine/change-player-turn)
                           (engine/take-space :space2)
                           (engine/change-player-turn)
                           (engine/take-space :space3)
                           (engine/change-player-turn)
                           (engine/take-space :space4)
                           (engine/change-player-turn)
                           (engine/take-space :space5)
                           (engine/change-player-turn)
                           (engine/take-space :space6)
                           (engine/change-player-turn)
                           (engine/take-space :space7)
                           (engine/change-player-turn)
                           (engine/take-space :space8)
                           (engine/all-remaining-spaces))))
          (it "snapshot: should have 0 spaces left"
              (should= #{}
                       (do (engine/take-space :space0)
                           (engine/change-player-turn)
                           (engine/take-space :space1)
                           (engine/change-player-turn)
                           (engine/take-space :space2)
                           (engine/change-player-turn)
                           (engine/take-space :space3)
                           (engine/change-player-turn)
                           (engine/take-space :space4)
                           (engine/change-player-turn)
                           (engine/take-space :space5)
                           (engine/change-player-turn)
                           (engine/take-space :space6)
                           (engine/change-player-turn)
                           (engine/take-space :space7)
                           (engine/change-player-turn)
                           (engine/take-space :space8)
                           (engine/all-remaining-spaces-memo @game-state)))))

(describe "space-available?:"
          (before (engine/reset-game))
          ;; -----
          (it "space should be available"
              (should= true
                       (do (engine/put! :board-size 3) 
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/space-available? :space3))))
          (it "snapshot: space should be available"
              (should= true
                       (do (engine/put! :board-size 3) 
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/space-available? @game-state :space3))))
          ;; -----
          (it "space should not be available"
              (should= false
                       (do (engine/put! :board-size 3) 
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/space-available? :space2))))
          (it "snapshot: space should not be available"
              (should= false
                       (do (engine/put! :board-size 3) 
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/space-available? @game-state :space2)))))

(describe "player-win?:"
          (before (engine/reset-game))
          (it "player1 should win on 3x3"
              (should= true
                       (do (engine/put! :board-size 3) 
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/player-win? :player1))))
          (it "snapshot: player1 should win on 3x3"
              (should= true
                       (do (engine/put! :board-size 3) 
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/player-win?-memo @game-state :player1))))
          ;; -----
          (it "player2 should win on 3x3"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/change-player-turn) 
                           (engine/take-space :space0)
                           (engine/take-space :space4)
                           (engine/take-space :space8)
                           (engine/player-win? :player2))))
          (it "snapshot: player2 should win on 3x3"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/change-player-turn) 
                           (engine/take-space :space0)
                           (engine/take-space :space4)
                           (engine/take-space :space8)
                           (engine/player-win?-memo @game-state :player2))))
          ;; -----
          (it "player1 should win 4x4"
              (should= true                      
                       (do (engine/put! :board-size 4)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/take-space :space3)
                           (engine/player-win? :player1))))
          (it "snapshot: player1 should win 4x4"
              (should= true                      
                       (do (engine/put! :board-size 4)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/take-space :space3)
                           (engine/player-win?-memo @game-state :player1))))
          ;; -----
          (it "player2 should win 4x4"
              (should= true                      
                       (do (engine/put! :board-size 4) 
                           (engine/change-player-turn)
                           (engine/take-space :space0)
                           (engine/take-space :space5)
                           (engine/take-space :space10)
                           (engine/take-space :space15)
                           (engine/player-win? :player2))))
          (it "snapshot: player2 should win 4x4"
              (should= true                      
                       (do (engine/put! :board-size 4) 
                           (engine/change-player-turn)
                           (engine/take-space :space0)
                           (engine/take-space :space5)
                           (engine/take-space :space10)
                           (engine/take-space :space15)
                           (engine/player-win?-memo @game-state :player2))))
          ;; -----
          (it "player1 should not win on 3x3"
              (should= nil
                       (do (engine/put! :board-size 3) 
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space3)
                           (engine/player-win? :player1))))
          (it "snapshot: player1 should not win on 3x3"
              (should= nil
                       (do (engine/put! :board-size 3) 
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space3)
                           (engine/player-win?-memo @game-state :player1))))
          ;; -----
          (it "player1 should not win on 4x4"
              (should= nil
                       (do (engine/put! :board-size 4) 
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/take-space :space4)
                           (engine/player-win? :player1))))
          (it "snapshot: player1 should not win on 4x4"
              (should= nil
                       (do (engine/put! :board-size 4) 
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/take-space :space4)
                           (engine/player-win?-memo @game-state :player1)))))

(describe "cats-game?:"
          (before (engine/reset-game))
          (it "should be cats game"
              (should= true
                       (do (engine/put! :board-size 3) 
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space4)
                           (engine/take-space :space5)
                           (engine/take-space :space6)
                           (engine/change-player-turn)
                           (engine/take-space :space2)
                           (engine/take-space :space3)
                           (engine/take-space :space7)
                           (engine/take-space :space8)
                           (engine/cats-game?))))
          (it "snapshot: should be cats game"
              (should= true
                       (do (engine/put! :board-size 3) 
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space4)
                           (engine/take-space :space5)
                           (engine/take-space :space6)
                           (engine/change-player-turn)
                           (engine/take-space :space2)
                           (engine/take-space :space3)
                           (engine/take-space :space7)
                           (engine/take-space :space8)
                           (engine/cats-game? @game-state))))
          ;; -----
          (it "should not be a cats game"
              (should= false
                       (do (engine/put! :board-size 3) 
                           ;; These three win
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           ;; --
                           (engine/take-space :space5)
                           (engine/take-space :space6)
                           (engine/change-player-turn)
                           (engine/take-space :space3)
                           (engine/take-space :space4)
                           (engine/take-space :space7)
                           (engine/take-space :space8)
                           (engine/cats-game?))))
          (it "snapshot: should not be a cats game"
              (should= false
                       (do (engine/put! :board-size 3) 
                           ;; These three win
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           ;; --
                           (engine/take-space :space5)
                           (engine/take-space :space6)
                           (engine/change-player-turn)
                           (engine/take-space :space3)
                           (engine/take-space :space4)
                           (engine/take-space :space7)
                           (engine/take-space :space8)
                           (engine/cats-game? @game-state)))))

(describe "game-over?:"
          (before (engine/reset-game))
          ;; -----
          (it "game should be over because player1 won"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/game-over?))))
          (it "snapshot: game should be over because player1 won"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/game-over? @game-state))))
          ;; -----
          (it "game should be over because player2 won"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/change-player-turn)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/game-over?))))
          (it "snapshot: game should be over because player2 won"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/change-player-turn)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/game-over? @game-state)))))

(describe "player-turn-sequence:"
          (before (engine/reset-game))
          (it "space should be able to be taken"
              (should= :space1
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space1)
                           ((engine/player-spaces :player1) :space1))))
          (it "snapshot: space should be able to be taken"
              (should= :space1
                       (do (engine/put! :board-size 3)
                           (engine/player-turn-sequence :space1)
                           ((engine/player-spaces @game-state :player1) :space1))))
          ;; -----
          (it "space should not be able to be taken - playe1 has already won"
              (should= nil
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/player-turn-sequence :space3)
                           ((engine/player-spaces :player1) :space3))))
          (it "snapshot: space should not be able to be taken - playe1 has already won"
              (should= nil
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/player-turn-sequence :space3)
                           ((engine/player-spaces @game-state :player1) :space3))))
          ;; -----
          (it "space should not be able to be taken - player2 has already won"
              (should= nil
                       (do (engine/put! :board-size 3)
                           (engine/change-player-turn)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/change-player-turn)
                           (engine/player-turn-sequence :space3)
                           ((engine/player-spaces :player1) :space3))))
          (it "snapshot: space should not be able to be taken - player2 has already won"
              (should= nil
                       (do (engine/put! :board-size 3)
                           (engine/change-player-turn)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/change-player-turn)
                           (engine/player-turn-sequence :space3)
                           ((engine/player-spaces @game-state :player1) :space3))))
          ;; -----
          (it "space should not be able to be taken, so it should still be player1's turn"
              (should= :player1
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/player-turn-sequence :space3)
                           (engine/which-player-turn?))))
          (it "snapshot: space should not be able to be taken, so it should still be player1's turn"
              (should= :player1
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/player-turn-sequence :space3)
                           (engine/which-player-turn? @game-state))))
          ;; -----
          (it "space be able to be taken, so it should be player2's turn"
              (should= :player2
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/player-turn-sequence :space3)
                           (engine/which-player-turn?))))
          (it "snapshot: space be able to be taken, so it should be player2's turn"
              (should= :player2
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/player-turn-sequence :space3)
                           (engine/which-player-turn? @game-state)))))
