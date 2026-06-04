package de.htwg.se.machikoro.remake

import de.htwg.se.machikoro.remake.controller.*
import de.htwg.se.machikoro.remake.model.Gamestate
import de.htwg.se.machikoro.remake.view.Tui.TUI

object main {
  def main(args: Array[String]): Unit = {
    ControllerV2.add(new TUI)
    ControllerV2.winCondition = _.hasWonTheGame()//Pattern Strategy the condition on which a player wins the game could also be _.money >= 50
    ControllerV2.startTurn(new Gamestate().createGame(2,"standart"))//hell_of_weat
    

    //because im lasy this is a singleton pattern anyway like controller is
  }
}
