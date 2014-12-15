(ns tic-tac-toe.core
  (:require [reagent.core :as reagent :refer [atom]]
            [tic-tac-toe.session :refer [put!]]
            [tic-tac-toe.logic :as logic]
            [tic-tac-toe.elements :as elem]))

;; This is a Reagent component and contains our entire tic tac toe application.
(defn page []
  (let [local-state (atom {:against-computer? true})]
    (fn []
      [:div.container
       [:div.row
        [:h2.text-center "Tic Tac Toe"]
        [:br]
        [elem/player-panel :player1 "info" local-state]
        [elem/player-panel :player2 "warning" local-state]]
       [:br]
       [elem/win-notification :player1 "info" "Player1 wins!"]
       [elem/win-notification :player2 "warning" "Player2 wins!"]
       [elem/cats-game-notification]
       [:div.row
        [:div {:class (elem/bootstrap-cols 3)}]
        [:div {:class (elem/bootstrap-cols 6)}
         [:div.text-center
          [elem/space 1 local-state]
          [elem/space 2 local-state]
          [elem/space 3 local-state]]
         [:div.text-center
          [elem/space 4 local-state]
          [elem/space 5 local-state]
          [elem/space 6 local-state]]
         [:div.text-center
          [elem/space 7 local-state]
          [elem/space 8 local-state]
          [elem/space 9 local-state]]]
        [:div {:class (elem/bootstrap-cols 3)}]]
       [:br]
       [:div.text-center 
        [elem/toggle-against local-state]
        [elem/restart]]
       ])))

;; Inside the `resources/public/index.html` file, there is an element with an "app" ID. This function replaces that element with the `page` component.
(reagent/render-component [page]
                          (.getElementById js/document "app"))
