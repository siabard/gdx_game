# Introduction to gdx_game

Clojure로 libgdx 라이브러리를 이용하여 2D/3D 게임을 만드는 과정을 설명한다.

# Project의 설정

libgdx 의존성을 추가한다.

```
(defproject gdx_game "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.badlogicgames.gdx/gdx "1.9.3"]
                 [com.badlogicgames.gdx/gdx-backend-lwjgl "1.9.3"]
                 [com.badlogicgames.gdx/gdx-box2d "1.9.3"]
                 [com.badlogicgames.gdx/gdx-box2d-platform "1.9.3"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-bullet "1.9.3"]
                 [com.badlogicgames.gdx/gdx-bullet-platform "1.9.3"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-platform "1.9.3"
                  :classifier "natives-desktop"]]
  :source-paths ["src" "src-common"]
  :aot [gdx-game.core.desktop.launcher]
  :main gdx-game.core.desktop.launcher
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
```

소스는 플랫폼별로 `src`에 필요한 기동코드를 넣고, `src-common`에는 게임 로직등 공통 소스를 둔다.

## launcher

```
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
```

위의 예제에서 보이는 대로, `LwjglApplicationConfiguration.forceExit(boolen)` 함수를 이용하여 
강제 종료시 발생되는 오류를 막았다.

Launcher에서 가장 중요한 일은 메인 게임 객체를 생성하는 것이며, 해당 게임 객체는 `gdx-game.core` 에서 정의한다.

## Core

```
(ns gdx-game.core
  (:import [com.badlogic.gdx Game Gdx Graphics Screen]
           [com.badlogic.gdx.graphics Color GL20]
           [com.badlogic.gdx.graphics.g2d BitmapFont]
           [com.badlogic.gdx.scenes.scene2d Stage]
           [com.badlogic.gdx.scenes.scene2d.ui Label Label$LabelStyle]))

(gen-class
 :name gdx-game.core.Game
 :extends com.badlogic.gdx.Game)

(def main-screen
  (let [stage (atom nil)]
    (proxy [Screen] []
      (show []
        (reset! stage (Stage.))
        (let [style (Label$LabelStyle. (BitmapFont.) (Color. 1 1 1 1))
              label (Label. "Hello World!" style)]
          (.addActor @stage label)))
      (render [delta]
        (.glClearColor (Gdx/gl) 0 0 0 0)
        (.glClear (Gdx/gl) GL20/GL_COLOR_BUFFER_BIT)
        (doto @stage
          (.act delta)
          (.draw)))
      (dispose [])
      (hide [])
      (pause [])
      (resize [w h])
      (resume []))))

(defn -create [^Game this]
  (.setScreen this main-screen))

```

이 부분에서는 `com.badlogic.gdx.Game`을 확장(extend)한 클래스를 정의한다. 주된 메서드는 `create`로
해당 메서드는 Launcher에서 Game 메인 프레임워크로의 진입점 역할을 한다.

## Screen

Game은 Screen 객체에서 모든 게임루프를 실행시킨다. 여기에서는 `main-screen`이라는 객체를 만들었으며, 주된 메서드는
`show`와 `render`이다.

`show`는 Screen이 메모리에 적재된 후 가장 처음으로 보여줄 때 진행할 일을 정의하고, `render`는 매 프레임별로 실행해야할
일을 정의한다. 여기에서 `delta`는 각 프레임간의 변동된 시간을 초로 반환한다.
