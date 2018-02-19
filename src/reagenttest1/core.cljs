(ns reagenttest1.core
    (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(println "This text is printed from src/reagenttest1/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom
                    {
                     :text "Tic Tac Toe"
                     :board
                     {:0 0
                      :1 0
                      :2 0
                      :3 0
                      :4 0
                      :5 0
                      :6 0
                      :7 0
                      :8 0
                      }
                     :winner :none
                     }
                    )
  )

(def circle-cx {
                :0 40 :1 120 :2 200
                :3 40 :4 120 :5 200
                :6 40 :7 120 :8 200
                })
(def circle-cy {
                :0 40 :1 40 :2 40
                :3 120 :4 120 :5 120
                :6 200 :7 200 :8 200
                })

(def rect-x {
             :0 10 :1 90 :2 170
             :3 10 :4 90 :5 170
             :6 10 :7 90 :8 170
             })

(def rect-y {
             :0 10 :1 10 :2 10
             :3 90 :4 90 :5 90
             :6 170 :7 170 :8 170
             })

(def crossleft-x1 {
                  :0 10 :1 90 :2 170
                   :3 10 :4 90 :5 170
                   :6 10 :7 90 :8 170
                   })

(def crossleft-y1 {
                   :0 10 :1 10 :2 10
                   :3 90 :4 90 :5 90
                   :6 170 :7 170 :8 170
                   })

(defn has-result? [[pos1 pos2 pos3]]
  (and
   (= (pos1 (:board @app-state))
      (pos2 (:board @app-state))
      (pos3 (:board @app-state))
      )
   (not (= 0 (pos1 (:board @app-state)))
        )
   )
  )

(def result-checks
  [
   {:positions [:0 :1 :2] :value :tophoriz}
   {:positions [:3 :4 :5] :value :midhoriz}
   {:positions [:6 :7 :8] :value :bothoriz}
   {:positions [:0 :3 :6] :value :leftvert}
   {:positions [:1 :4 :7] :value :midvert}
   {:positions [:2 :5 :8] :value :rightvert}
   {:positions [:0 :4 :8] :value :leftdiag}
   {:positions [:2 :4 :6] :value :rightdiag}
   ]
  )

(defn detect-result []
  (let [possible-result (first
                         (filter #(has-result? (:positions %))
                                 result-checks)
                         )
        ]
    (if (not (= nil possible-result))
      (swap! app-state assoc :winner (:value possible-result))
      )
    )
  )

(defn set-mark [pos val]
  (do
    (swap! app-state assoc-in [:board pos] val)
    (detect-result)
    )
  )


(defn random-move []
  (set-mark
   (let [the-board (:board @app-state)]
     (rand-nth
      (filter #(= 0
                  (% the-board)
                  )
              (keys the-board)
              )
      )
     )
   2
   )
  )

(defn generate-mark [pos]
  (case (pos (:board @app-state))
    1 [
       [:circle {:cx (pos circle-cx) :cy (pos circle-cy) :r 20 :style {:stroke "black" :stroke-width 3 :fill "white"}}]
       ]
    2 [
       [:line {:x1 (pos crossleft-x1) :y1 (pos crossleft-y1) :x2 (+ 60 (pos crossleft-x1)) :y2 (+ 60 (pos crossleft-y1)) :style {:stroke "black" :stroke-width 3}}]
       [:line {:x1 (+ 60 (pos crossleft-x1)) :y1 (pos crossleft-y1) :x2 (pos crossleft-x1) :y2 (+ 60 (pos crossleft-y1)) :style {:stroke "black" :stroke-width 3}}]
       ]
    [
     [:rect {:x (pos rect-x) :y (pos rect-y) :width 60 :height 60 :style {:fill "white"} :on-click #(do (set-mark pos 1) (random-move)) }]
     ]
    )
  )

(defn generate-marks [board]
  (mapcat generate-mark (keys board))
  )

(def winner-line-x1 {
                     :tophoriz 0
                     :midhoriz 0
                     :bothoriz 0
                     :leftvert 40
                     :midvert 120
                     :rightvert 200
                     :leftdiag 0
                     :rightdiag 240
                     }
  )

(def winner-line-y1 {
                     :tophoriz 40
                     :midhoriz 120
                     :bothoriz 200
                     :leftvert 0
                     :midvert 0
                     :rightvert 0
                     :leftdiag 0
                     :rightdiag 0
                     }
  )

(def winner-line-x2 {
                     :tophoriz 240
                     :midhoriz 240
                     :bothoriz 240
                     :leftvert 40
                     :midvert 120
                     :rightvert 200
                     :leftdiag 240
                     :rightdiag 0
                     }
  )

(def winner-line-y2 {
                     :tophoriz 40
                     :midhoriz 120
                     :bothoriz 200
                     :leftvert 240
                     :midvert 240
                     :rightvert 240
                     :leftdiag 240
                     :rightdiag 240
                     }
  )

(defn generate-winner [winner]
  (if (not (= :none winner)
           )
    [:line {:x1 (winner winner-line-x1) :y1 (winner winner-line-y1)
            :x2 (winner winner-line-x2) :y2 (winner winner-line-y2)
            :style {:stroke "blue" :stroke-width 3}
            }
     ]
    )
  )
(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Click to play with circles (computer plays crosses)"]
   [:p
    [:a {:href "https://github.com/debamitro/tictactoe-reagent" :target "_blank"} "Source code"]
    ]
   [:button {:on-click #(swap! app-state assoc
                               :board
                               {:0 0 :1 0 :2 0 :3 0 :4 0 :5 0 :6 0 :7 0 :8 0}
             :winner :none)
             } "Clear"]
   [:button {:on-click random-move} "Manual Move by computer"]
   [:svg {:width 300 :height 300}
    [:line {:x1 80 :y1 0 :x2 80 :y2 240 :style {:stroke "black" :stroke-width 2}}]
    [:line {:x1 160 :y1 0 :x2 160 :y2 240 :style {:stroke "black" :stroke-width 2}}]
    [:line {:x1 0 :y1 80 :x2 240 :y2 80 :style {:stroke "black" :stroke-width 2}}]
    [:line {:x1 0 :y1 160 :x2 240 :y2 160 :style {:stroke "black" :stroke-width 2}}]
    (generate-marks (:board @app-state))
    (generate-winner (:winner @app-state))
    ]
   [:pre "Debug\n" (str @app-state)]
   ])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
