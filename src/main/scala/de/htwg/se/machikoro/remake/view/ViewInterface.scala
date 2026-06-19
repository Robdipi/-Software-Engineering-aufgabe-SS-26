package de.htwg.se.machikoro.remake.view

import de.htwg.se.machikoro.remake.controller.main.ViewObserver
import de.htwg.se.machikoro.remake.model.Data.Gamestate
import scalafx.application.JFXApp3

trait ViewInterface extends ViewObserver{}

trait StarterInterface extends JFXApp3 {
  def startView(gamestate: Gamestate): Unit
}
