package de.htwg.se.machikoro.remake.view.Gui

import de.htwg.se.machikoro.remake.controller.main.ControllerInterface
import de.htwg.se.machikoro.remake.controller.main.impl1.ControllerV2
import de.htwg.se.machikoro.remake.model.Gamestate
import de.htwg.se.machikoro.remake.model.initialization.gameInitializationSystem
import de.htwg.se.machikoro.remake.model.initialization.impl1.Game
import scalafx.application.JFXApp3
import scalafx.scene.Scene


object MachiKoroApp extends JFXApp3 {
  var controller: ControllerInterface = _
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