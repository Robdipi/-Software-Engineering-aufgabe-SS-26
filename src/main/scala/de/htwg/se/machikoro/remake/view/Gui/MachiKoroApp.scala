package de.htwg.se.machikoro.remake.view.Gui

import de.htwg.se.machikoro.remake.controller.main.ControllerV2
import de.htwg.se.machikoro.remake.model.Gamestate
import de.htwg.se.machikoro.remake.model.initialization.Game

import scalafx.application.JFXApp3
import scalafx.scene.Scene

/**
 * ScalaFX-Startpunkt fuer die GUI.
 * Wird aus main.scala mit --gui gestartet.
 */
object MachiKoroApp extends JFXApp3 {
  var controller: ControllerV2 = _
  var startGamestate: Gamestate = _

  override def start(): Unit = {
    if controller == null then controller = new ControllerV2(_.hasWonTheGame())
    if startGamestate == null then startGamestate = new Game()(2, "standart")

    val gui = new GUI(controller)

    stage = new JFXApp3.PrimaryStage {
      title = "Machi Koro"
      scene = new Scene(1400, 900) {
        root = gui.root
      }
      minWidth = 1180
      minHeight = 760
    }

    controller.startTurn(startGamestate)
  }
}
