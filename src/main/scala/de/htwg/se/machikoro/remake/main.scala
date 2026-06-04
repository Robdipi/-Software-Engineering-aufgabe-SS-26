package de.htwg.se.machikoro.remake

import de.htwg.se.machikoro.remake.controller.*
import de.htwg.se.machikoro.remake.model.Gamestate
import de.htwg.se.machikoro.remake.view.Tui.TUI

object main {
  def main(args: Array[String]): Unit = {
    Controller.add(new TUI)
    Controller.gamestate = new Gamestate().initializeStandartGame(2)
    Controller.startphase()

  }
}
