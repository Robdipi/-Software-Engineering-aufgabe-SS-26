package de.htwg.se.machikoro.remake.view.Gui

import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.main.ControllerInterface
import de.htwg.se.machikoro.remake.model.Data.Gamestate
import de.htwg.se.machikoro.remake.view.StarterInterface
import scalafx.application.JFXApp3
import scalafx.scene.Scene

class MachiKoroApp @Inject()(val controller: ControllerInterface) extends StarterInterface {

  private var gs: Gamestate = _

  override def startView(gamestate: Gamestate): Unit = {
    gs = gamestate
  }

  override def start(): Unit = {
    MachiKoroFxApp.controller = controller
    MachiKoroFxApp.startGamestate = gs
    MachiKoroFxApp.main(Array.empty)
  }
}

object MachiKoroFxApp extends JFXApp3 {
  var controller: ControllerInterface = _
  var startGamestate: Gamestate = _

  override def start(): Unit = {
    val gui = new GUI(controller)

    stage = new JFXApp3.PrimaryStage {
      title = "Machi Koro"
      scene = new Scene(1400, 900) {
        root = gui.root
      }
      minWidth = 1180
      minHeight = 760
    }

    controller.add(gui)
    controller.startTurn(startGamestate)
  }
}