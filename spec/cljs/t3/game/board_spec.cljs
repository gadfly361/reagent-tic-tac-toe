(ns t3.game.board-spec
  (:require-macros [speclj.core :refer [describe it should=]])
  (:require [speclj.core]
            [t3.game.board :as board]))

(describe "board-spaces"
          (it "3x3 should create 9 spaces"
              (should= (list :space0 :space1 :space2
                             :space3 :space4 :space5
                             :space6 :space7 :space8)
                       (board/board-spaces 3)))
          (it "4x4 should create 16 spaces"
              (should= (list :space0 :space1 :space2 :space3
                             :space4 :space5 :space6 :space7
                             :space8 :space9 :space10 :space11
                             :space12 :space13 :space14 :space15)
                       (board/board-spaces 4))))

(describe "board-rows"
          (it "3x3 should create 3 rows of 3 spaces each"
              (should= (list '(:space0 :space1 :space2)
                             '(:space3 :space4 :space5)
                             '(:space6 :space7 :space8))
                       (board/board-rows 3)))
          (it "4x4 should create 4 rows of 4 spaces each"
              (should= (list '(:space0 :space1 :space2 :space3)
                             '(:space4 :space5 :space6 :space7)
                             '(:space8 :space9 :space10 :space11)
                             '(:space12 :space13 :space14 :space15))
                       (board/board-rows 4))))

(describe "board-columns"
          (it "3x3 should create 3 columns of 3 spaces each"
              (should= (list '(:space0 :space3 :space6)
                             '(:space1 :space4 :space7)
                             '(:space2 :space5 :space8))
                       (board/board-columns 3)))
          (it "4x4 should create 4 columns of 4 spaces each"
              (should= (list '(:space0 :space4 :space8 :space12)
                             '(:space1 :space5 :space9 :space13)
                             '(:space2 :space6 :space10 :space14)
                             '(:space3 :space7 :space11 :space15))
                       (board/board-columns 4))))

(describe "board-corners"
          (it "3x3 should have four corners"
              (should= #{:space0 :space2 :space6 :space8}
                       (board/board-corners 3)))
          (it "4x4 should have 4 corners"
              (should= #{:space0 :space3 :space12 :space15}
                       (board/board-corners 4))))

(describe "horizontal-wins"
          (it "3x3 should create 3 horizontal wins"
              (should= (list #{:space0 :space1 :space2}
                             #{:space3 :space4 :space5}
                             #{:space6 :space7 :space8})
                       (board/horizontal-wins 3)))
          (it "4x4 should create 4 horizontal wins"
              (should= (list #{:space0 :space1 :space2 :space3}
                             #{:space4 :space5 :space6 :space7}
                             #{:space8 :space9 :space10 :space11}
                             #{:space12 :space13 :space14 :space15})
                       (board/horizontal-wins 4))))

(describe "vertical-wins"
          (it "3x3 should create 3 vertical wins"
              (should= (list #{:space0 :space3 :space6}
                             #{:space1 :space4 :space7}
                             #{:space2 :space5 :space8})
                       (board/vertical-wins 3)))
          (it "4x4 should create 4 vertical wins"
              (should= (list #{:space0 :space4 :space8 :space12}
                             #{:space1 :space5 :space9 :space13}
                             #{:space2 :space6 :space10 :space14}
                             #{:space3 :space7 :space11 :space15})
                       (board/vertical-wins 4))))

(describe "diagonal-wins"
          (it "3x3 should create 2 diagonal wins"
              (should= (list #{:space0 :space4 :space8}
                             #{:space2 :space4 :space6})
                       (board/diagonal-wins 3)))
          (it "4x4 should create 2 diagonal wins"
              (should= (list #{:space0 :space5 :space10 :space15}
                             #{:space3 :space6 :space9 :space12})
                       (board/diagonal-wins 4))))

(describe "board-wins"
          (it "3x3 should create 8 board wins"
              (should= [;; horizontal
                         #{:space0 :space1 :space2}
                         #{:space3 :space4 :space5}
                         #{:space6 :space7 :space8} 
                         ;;vertical
                         #{:space0 :space3 :space6}
                         #{:space1 :space4 :space7}
                         #{:space2 :space5 :space8}
                         ;; diagonal
                         #{:space0 :space4 :space8}
                         #{:space2 :space4 :space6}]
                       (board/board-wins 3)))
          (it "4x4 should create 10 board wins"
              (should= [;; horizontal
                         #{:space0 :space1 :space2 :space3}
                         #{:space4 :space5 :space6 :space7}
                         #{:space8 :space9 :space10 :space11}
                         #{:space12 :space13 :space14 :space15}
                         ;;vertical
                         #{:space0 :space4 :space8 :space12}
                         #{:space1 :space5 :space9 :space13}
                         #{:space2 :space6 :space10 :space14}
                         #{:space3 :space7 :space11 :space15}
                         ;; diagonal
                         #{:space0 :space5 :space10 :space15}
                         #{:space3 :space6 :space9 :space12}]
                       (board/board-wins 4))))
