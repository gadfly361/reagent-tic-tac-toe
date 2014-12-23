(ns t3.core
  (:require [reagent.core :as reagent]
            [t3.ui.view.page :refer [tic-tac-toe-page]]))

;; initialize app
(defn main []
  (reagent/render-component [tic-tac-toe-page]
                          (.getElementById js/document "app")))
