(ns t3.game.board)

(defn board-spaces 
  "Create board spaces based on the width of the board. For example, 
  passing in an argument of 3 would make a board of 9 spaces"
  [x]
  (let [spaces (range (* x x))]
    (map #(keyword (str "space" %)) spaces)))

(defn board-rows [x]
  (partition x (board-spaces x)))

(defn board-columns [x]
  (for [n (range x)]
    (map first (map #(drop n %) (board-rows x)))))

(defn board-corners [x]
  (let [br (board-rows x)
        top-left (first (first br))
        top-right (last (first br))
        bottom-left (first (last br))
        bottom-right (last (last br))]
    #{top-left top-right bottom-left bottom-right}))

(defn horizontal-wins [x]
  (map #(into #{} %)(board-rows x)))

(defn vertical-wins [x]
  (map #(into #{} %) (board-columns x)))

(defn diagonal-wins [x]
  (let [spaces (board-spaces x)
        left->right (->> spaces
                         (take-nth (+ x 1))
                         (into #{}))
        right->left (->> spaces
                         (take-nth (- x 1))
                         (drop 1)
                         (drop-last)
                         (into #{}))]
    (list left->right
          right->left)))

(defn board-wins [x]
  (let [list-of-wins (concat (horizontal-wins x)
                             (vertical-wins x)
                             (diagonal-wins x))]
    (into [] list-of-wins)))
