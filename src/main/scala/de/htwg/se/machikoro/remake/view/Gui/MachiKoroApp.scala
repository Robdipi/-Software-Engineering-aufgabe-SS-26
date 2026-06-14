package de.htwg.se.machikoro.remake.view.Gui

import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.main.ControllerInterface
import de.htwg.se.machikoro.remake.model.Gamestate
import de.htwg.se.machikoro.remake.model.initialization.gameInitializationSystem
import de.htwg.se.machikoro.remake.view.starterInterface
import scalafx.application.JFXApp3
import scalafx.scene.Scene


class MachiKoroApp @Inject() extends starterInterface {
  


  def startView(): Unit = {
    start()
  }

  override def start(): Unit = {
    val gui = new GUI(controller)

    stage = new JFXApp3.PrimaryStage {
      title = "Machi Koro"
      scene = new Scene(1400, 900) {
        root = gui.root
      }
    }

    controller.startTurn(startGamestate)
  }
}