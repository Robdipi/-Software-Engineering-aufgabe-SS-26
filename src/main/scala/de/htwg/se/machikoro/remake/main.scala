package de.htwg.se.machikoro.remake

import de.htwg.se.machikoro.remake.controller.*
import de.htwg.se.machikoro.remake.model.Gamestate
import de.htwg.se.machikoro.remake.view.Tui.TUI

object main {
  def main(args: Array[String]): Unit = {
    controllerMain.add(new TUI)
    val gameState = new Gamestate().initializeStandartGame(2)
    controllerMain.gameloop(gameState)
  }
}
