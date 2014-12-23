(ns t3.ui.view.elements
  (:require [t3.ui.api :as api]
            [clojure.string :refer [capitalize]]))

;; ----------
(defn space [space]
  (let [owner (cond (space (api/player-spaces :player1)) :player1
                    (space (api/player-spaces :player2)) :player2
                    :else :default)
        image (str (name owner))
        width (/ 95 (api/board-size))]
    ^{:key (gensym "img")}
    [:img {:src (str "img/" image ".png")
           :style {:width (str width "%")}
           :on-click #(api/click-space space)}]))

(defn div-center [x]
  [:div.text-center x])

(defn table []
  [:div (for [row (api/board-rows)]
          ^{:key (gensym "row")}
          [div-center (map space row)])])

;; ----------
(defn turn-icon [player]
  (when-not (api/game-over?)
    (if (= player (api/which-player-turn?)) [:i.fa.fa-long-arrow-left])))

(defn win-icon [player]
  (when (api/player-win? player)
    [:i.fa.fa-star]))

(defn panel-header [player]
  [:div.panel-heading [:h4 (capitalize (str (name player) " "))
                       [turn-icon player]
                       [win-icon player]]])

(defn display-list [lst]
  (interpose ", " (map name lst)))

(defn panel-body [player]
  [:div.panel-body [:h6 "Spaces: " (display-list (api/player-spaces player))]])

(defn player-panel [player panel-type]
  [:div
   [(keyword (str "div.panel.panel-" panel-type))
    [panel-header player]
    [panel-body player]]])

;; ----------
(defn win-notification [player alert-type]
  (when (api/player-win? player) 
    [(keyword (str "h5.alert.alert-" alert-type)) (str (capitalize (name player)) " wins!")]))

(defn cats-game-notification []
  (when (api/cats-game?)
    [:h5.alert.alert-danger "Cat's game!"]))

;; ----------
(defn reset-resize [n]
  (let [reset-resize (fn [n] (do (api/reset-game)
                                 (api/set-board-size n)))]
    [:span [:div.btn.btn-success {:on-click #(reset-resize n)} [:h5 (str n "x" n)]]
     " "]))

(defn reset-game-resize-board []
    [:div.text-center
     [:h5 "Restart Game: Pick a board size"]
     [reset-resize 3]
     [reset-resize 4]
     [reset-resize 5]])

;; ----------
(defn toggle-playing-against []
  (when (or (api/game-over?)
            (and (empty? (api/player-spaces :player1)) (empty? (api/player-spaces :player2))))
    [:div.text-center
     [:div.btn.btn-primary.text-center {:on-click #(api/toggle-playing-against)}
      [:h5 (if (api/playing-against-computer?) "Press to play against human"
               "Press to play against computer")]]]))
