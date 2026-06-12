package de.htwg.se.machikoro.remake.view.Gui

import de.htwg.se.machikoro.remake.controller.main.ControllerV2
import de.htwg.se.machikoro.remake.model.Gamestate
import de.htwg.se.machikoro.remake.model.initialization.{Game, gameInitializationSystem}
import scalafx.application.JFXApp3
import scalafx.scene.Scene


object MachiKoroApp extends JFXApp3 {
  var controller: ControllerV2 = _
  var startGamestate : Gamestate = _;
  override def start(): Unit = {
    val gameInitializationSystem: gameInitializationSystem = new Game()

    val gui = new GUI(MachiKoroApp.controller)

    stage = new JFXApp3.PrimaryStage {
      title = "Machi Koro"
      scene = new Scene(1400, 900) {
        root = gui.root
      }
    }

    MachiKoroApp.controller.startTurn(startGamestate)
  }
}