(ns t3.ui.view.page
  (:require [t3.ui.view.elements :as elem]))

(defn tic-tac-toe-page []
  [:div.container
   [:div.row
    [:h2.text-center "Tic Tac Toe"]
    [:br]
    [:div.col-md-2]
    [:div.col-md-4 [elem/player-panel :player1 "info"]]
    [:div.col-md-4 [elem/player-panel :player2 "warning"]]
    [:div.col-md-2]
    [:br]]
   [:div.row
    [:div.col-md-2]
    [:div.col-md-8
    [elem/win-notification :player1 "info"]
    [elem/win-notification :player2 "warning"]
    [elem/cats-game-notification]]
    [:div.col-md-2]]
   [:div.row
    [:div.col-md-4]
    [:div.col-md-4
     [elem/table]]
    [:div.col-md-4]]
   [:br]
   [:div.row
    [:div.col-md-4]
    [:div.col-md-4.well
     [elem/reset-game-resize-board]
     [:br]
     [elem/toggle-playing-against]]
    [:div.col-md-4]]
   ])
