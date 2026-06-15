package de.htwg.se.machikoro.remake.view

import de.htwg.se.machikoro.remake.controller.main.{ControllerInterface, viewObserver}
import de.htwg.se.machikoro.remake.model.Data.Gamestate
import scalafx.application.JFXApp3

trait ViewInterface extends viewObserver{}

trait starterInterface extends JFXApp3 {
  def startView(gamestate: Gamestate): Unit
}
