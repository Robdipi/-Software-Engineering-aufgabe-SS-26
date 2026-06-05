package de.htwg.se.machikoro.remake

import de.htwg.se.machikoro.remake.controller.*
import de.htwg.se.machikoro.remake.model.Gamestate
import de.htwg.se.machikoro.remake.view.Tui.TUI
import de.htwg.se.machikoro.remake.view.Gui.{GUI, MachiKoroApp}
import scalafx.application.JFXApp3


object main{
  def main(args: Array[String]): Unit = {
    //make a flag system as that would be cool?
    if (true || args.contains("--gui")) { // sbt "run --gui" to start with gui
      MachiKoroApp.main(args)
    }else{
      ControllerV2.add(new GUI)
      ControllerV2.winCondition = _.hasWonTheGame() //Pattern Strategy the condition on which a player wins the game could also be _.money >= 50
      ControllerV2.startTurn(new Gamestate().createGame(2, "standart")) //hell_of_weat


      //because im lasy this is a singleton pattern anyway like controller is
    }

  }
}
