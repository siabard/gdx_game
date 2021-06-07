(ns gdx-game.core.desktop.launcher
  (:require [gdx-game.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication LwjglApplicationConfiguration]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main []
  (let [config (LwjglApplicationConfiguration.)]
    (set! (.forceExit config) false)
    (set! (.title config) "Game")
    (LwjglApplication. (gdx-game.core.Game. ) config)
    (Keyboard/enableRepeatEvents true)))
