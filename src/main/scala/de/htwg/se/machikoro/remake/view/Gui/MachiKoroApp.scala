package de.htwg.se.machikoro.remake.view.Gui



import de.htwg.se.machikoro.remake.controller.ControllerV2
import de.htwg.se.machikoro.remake.model.Gamestate
import scalafx.application.JFXApp3
import scalafx.scene.Scene

object MachiKoroApp extends JFXApp3 {

  override def start(): Unit = {

    val gui = new GUI()
    ControllerV2.add(gui)
    ControllerV2.winCondition = _.hasWonTheGame()

    stage = new JFXApp3.PrimaryStage {
      title = "Machi Koro"
      scene = new Scene(1400, 900) {
        root = gui.root
      }
    }
    ControllerV2.startTurn(
      new Gamestate().createGame(2, "standart")
    )
  }
}