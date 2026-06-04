package de.htwg.se.machikoro.remake

import de.htwg.se.machikoro.remake.controller.*
import de.htwg.se.machikoro.remake.model.Gamestate
import de.htwg.se.machikoro.remake.view.Tui.TUI

object main {
  def main(args: Array[String]): Unit = {
    Controller.add(new TUI)
    Controller.gamestate = new Gamestate().createGame(2,"standart")//hell_of_weat
    Controller.winCondition = _.hasWonTheGame()//Pattern Strategy the condition on which a player wins the game could also be _.money >= 50
    Controller.startphase()

  }
}
