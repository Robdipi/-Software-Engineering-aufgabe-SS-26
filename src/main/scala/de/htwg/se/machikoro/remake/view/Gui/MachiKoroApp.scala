package de.htwg.se.machikoro.remake.view.Gui



import de.htwg.se.machikoro.remake.controller.ControllerV2
import de.htwg.se.machikoro.remake.model.{Game, Gamestate}
import scalafx.application.JFXApp3
import scalafx.scene.Scene

object MachiKoroApp extends JFXApp3 {

  override def start(): Unit = {
    val controller = ControllerV2(_.hasWonTheGame())
    val gui = new GUI(controller)
    stage = new JFXApp3.PrimaryStage {
      title = "Machi Koro"
      scene = new Scene(1400, 900) {
        root = gui.root
      }
    }
    controller.startTurn(Game(2, "standart")) //hell_of_weat

  }
}