(ns t3.ui.api-spec
  (:require-macros [speclj.core :refer [describe it should= before]])
  (:require [speclj.core]
            [t3.ui.api :as api]
            [t3.game.engine :as engine :refer [game-state playing-against-state]]))

(describe "board-spaces:"
          (before (engine/reset-game))
          (it "3x3 should return list of 9 spaces"
              (should= (list :space0 :space1 :space2
                             :space3 :space4 :space5
                             :space6 :space7 :space8)
                       (do (engine/put! :board-size 3)
                           (api/board-spaces))))
          (it "4x4 should return list of 16 spaces"
              (should= (list :space0 :space1 :space2 :space3
                             :space4 :space5 :space6 :space7
                             :space8 :space9 :space10 :space11
                             :space12 :space13 :space14 :space15)
                       (do (engine/put! :board-size 4)
                           (api/board-spaces)))))

(describe "board-rows"
          (before (engine/reset-game))
          (it "3x3 should create 3 rows of 3 spaces each"
              (should= (list '(:space0 :space1 :space2)
                             '(:space3 :space4 :space5)
                             '(:space6 :space7 :space8))
                       (do (engine/put! :board-size 3)
                           (api/board-rows))))
          (it "4x4 should create 4 rows of 4 spaces each"
              (should= (list '(:space0 :space1 :space2 :space3)
                             '(:space4 :space5 :space6 :space7)
                             '(:space8 :space9 :space10 :space11)
                             '(:space12 :space13 :space14 :space15))
                       (do (engine/put! :board-size 4)
                           (api/board-rows)))))

(describe "board-rows"
          (before (engine/reset-game))
          (it "3x3 should create 3 rows of 3 spaces each"
              (should= (list '(:space0 :space1 :space2)
                             '(:space3 :space4 :space5)
                             '(:space6 :space7 :space8))
                       (do (engine/put! :board-size 3)
                           (api/board-rows))))
          (it "4x4 should create 4 rows of 4 spaces each"
              (should= (list '(:space0 :space1 :space2 :space3)
                             '(:space4 :space5 :space6 :space7)
                             '(:space8 :space9 :space10 :space11)
                             '(:space12 :space13 :space14 :space15))
                       (do (engine/put! :board-size 4)
                           (api/board-rows)))))

(describe "player-spaces:"
          (before (engine/reset-game))
          (it "player1 should have :space0"
              (should= (sorted-set :space0)
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (api/player-spaces :player1))))
          (it "player1 should have :space1 and :space2"
              (should= (sorted-set :space1 :space2)
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (api/player-spaces :player1))))
          (before (engine/reset-game))
          (it "player2 should have :space0"
              (should= (sorted-set :space0)
                       (do (engine/put! :board-size 3)
                           (engine/change-player-turn)
                           (engine/take-space :space0)
                           (api/player-spaces :player2))))
          (it "player2 should have :space1 and :space2"
              (should= (sorted-set :space1 :space2)
                       (do (engine/put! :board-size 3)
                           (engine/change-player-turn)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (api/player-spaces :player2)))))

(describe "player-win?:"
          (before (engine/reset-game))
          (it "player1 should win"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (api/player-win? :player1))))
          (it "player1 shouldn't win"
              (should= nil
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space0)
                           (engine/take-space :space1)
                           (engine/take-space :space3)
                           (api/player-win? :player1))))
          (before (engine/reset-game))
          (it "player2 should win"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/change-player-turn)
                           (engine/take-space :space0)
                           (engine/take-space :space4)
                           (engine/take-space :space8)
                           (api/player-win? :player2))))
          (it "player2 shouldn't win"
              (should= nil
                       (do (engine/put! :board-size 3)
                           (engine/change-player-turn)
                           (engine/take-space :space0)
                           (engine/take-space :space3)
                           (engine/take-space :space7)
                           (api/player-win? :player2)))))

(describe "cats-game?:"
          (before (engine/reset-game))
          (it "should be a cat's game"
              (should= true
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/take-space :space3)
                           (engine/take-space :space8)
                           (engine/change-player-turn)
                           (engine/take-space :space0)
                           (engine/take-space :space4)
                           (engine/take-space :space5)
                           (engine/take-space :space6)
                           (engine/take-space :space7)
                           (api/cats-game?))))
          (it "should not be a cat's game - player1 won"
              (should= false
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/take-space :space3)
                           (engine/take-space :space5)
                           (engine/take-space :space8)
                           (engine/change-player-turn)
                           (engine/take-space :space0)
                           (engine/take-space :space4)
                           (engine/take-space :space6)
                           (engine/take-space :space7)
                           (api/cats-game?))))
          (it "should not be a cat's game - spaces still remain"
              (should= false
                       (do (engine/put! :board-size 3)
                           (engine/take-space :space1)
                           (engine/take-space :space2)
                           (engine/take-space :space3)
                           (engine/take-space :space8)
                           (engine/change-player-turn)
                           (engine/take-space :space0)
                           (engine/take-space :space4)
                           (engine/take-space :space6)
                           (engine/take-space :space7)
                           (api/cats-game?)))))

(describe "click-space:"
          (before (engine/reset-game))
          (before (reset! playing-against-state {:playing-against-computer? false}))
          (it "player1 should take :space0 and it should be player2's turn"
              (should= true
               (do (engine/put! :board-size 3)
                   (api/click-space :space0)
                   (and (= (sorted-set :space0) 
                           (engine/other-player-spaces (if (@game-state :player1-turn?) :player1 :player2)))
                        (= :player2 (engine/which-player-turn?))
                        ))))
          (it "if player1 takes a space, it should not be player1's turn still"
              (should= false
               (do (engine/put! :board-size 3)
                   (api/click-space :space0)
                   (and (= (sorted-set :space0) 
                           (engine/other-player-spaces (if (@game-state :player1-turn?) :player1 :player2)))
                        (= :player1 (engine/which-player-turn?))
                        ))))
          (it "if player1 takes :space0, and then player2 tries to take :space0, it should still be player2's turn and player2 should not own :space0"
              (should= true
               (do (engine/put! :board-size 3)
                   (api/click-space :space0)
                   (api/click-space :space0)
                   (and (= (sorted-set :space0) 
                           (engine/player-spaces :player1))
                        (= (sorted-set )
                           (engine/player-spaces :player2))
                        (= :player2 (engine/which-player-turn?))
                        )))))

(describe "toggle-playing-against & playing-against-computer?"
          (before (engine/reset-game))
          (before (reset! playing-against-state {:playing-against-computer? true}))
          (it "should start playing against a computer"
              (should= true
                       (api/playing-against-computer?)))
          (it "should toggle to playing against a huamn"
              (should= false
                       (do (api/toggle-playing-against)
                           (api/playing-against-computer?))))
          (it "should toggle to playing against a human and be unaffected by reset game"
              (should= false
                       (do (api/toggle-playing-against)
                           (api/reset-game)
                           (api/playing-against-computer?)))))
