package de.htwg.se.machikoro.remake

import de.htwg.se.machikoro.remake.controller.*
import de.htwg.se.machikoro.remake.controller.main.ControllerV2
import de.htwg.se.machikoro.remake.controller.mementoPatern.mementoCreator
import de.htwg.se.machikoro.remake.model.Gamestate
import de.htwg.se.machikoro.remake.model.initialization.{Game, gameInitializationSystem}
import de.htwg.se.machikoro.remake.view.Tui.TUI
import de.htwg.se.machikoro.remake.view.Gui.GUI
import de.htwg.se.machikoro.remake.view.Gui.*
import scalafx.application.JFXApp3


object main{
  def main(args: Array[String]): Unit = {
    val GameInitializationSystem: gameInitializationSystem = new Game()
    var startGamestate = GameInitializationSystem(2, "standart") //hell_of_weat
    val controller = ControllerV2(_.hasWonTheGame())
    

    //make a flag system as that would be cool?
    if (args.contains("--mem")) {// loads the last save that was left in the old saves and then deletes the old saves
      startGamestate = mementoCreator.loadGamesave(controller.undoManager).getOrElse(startGamestate) // nothing changes if loadgame failed
    }
    mementoCreator.flushSavefiles() //delete all save files otherwise
    if (args.contains("--gui")) { // sbt "run --gui" to start with gui
      //start ui
      MachiKoroApp.controller = controller
      MachiKoroApp.startGamestate = startGamestate
      MachiKoroApp.main(args)
    }else{
      val gui = new TUI(controller)
      //controller.winCondition = _.hasWonTheGame() //Pattern Strategy the condition on which a player wins the game could also be _.money >= 50
      controller.startTurn(startGamestate)


      //because im lasy this is a singleton pattern anyway like controller is
    }

  }
}
