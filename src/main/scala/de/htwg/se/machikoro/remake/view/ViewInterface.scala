package de.htwg.se.machikoro.remake.view

import de.htwg.se.machikoro.remake.controller.main.{ControllerInterface, viewObserver}
import de.htwg.se.machikoro.remake.model.Gamestate
import scalafx.application.JFXApp3

trait ViewInterface extends viewObserver{}
trait starterInterface extends JFXApp3 {
  var controller: ControllerInterface = _
  var startGamestate: Gamestate = _;
  def startView(): Unit
}
