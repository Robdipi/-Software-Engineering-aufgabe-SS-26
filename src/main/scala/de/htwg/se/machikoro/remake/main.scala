package de.htwg.se.machikoro.remake

import com.google.inject.Guice
import de.htwg.se.machikoro.remake.controller.commandPattern.UndoManagerInterface
import de.htwg.se.machikoro.remake.controller.main.{ControllerInterface, WinCondition}
import de.htwg.se.machikoro.remake.controller.main.impl1.ControllerV2
import de.htwg.se.machikoro.remake.controller.mementoPatern.mementoCreator
import de.htwg.se.machikoro.remake.model.Gamestate
import de.htwg.se.machikoro.remake.model.initialization.gameInitializationSystem
import de.htwg.se.machikoro.remake.view.Tui.TUI
import de.htwg.se.machikoro.remake.view.Gui.{GUI, MachiKoroApp}
import scalafx.application.JFXApp3



object main{
  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(new AppModule(args))
    
    val GameInitializationSystem: gameInitializationSystem = injector.getInstance(classOf[gameInitializationSystem])
    var startGamestate = GameInitializationSystem(2, "standart") //hell_of_weat
    
    val controller = injector.getInstance(classOf[ControllerInterface])
     controller.winCondition = injector.getInstance(classOf[WinCondition])
    controller.undoManager = injector.getInstance(classOf[UndoManagerInterface])

    if (args.contains("--mem")) {// loads the last save that was left in the old saves and then deletes the old saves
      startGamestate = mementoCreator.loadGamesave(controller.undoManager).getOrElse(startGamestate) // nothing changes if loadgame failed
    }
    mementoCreator.flushSavefiles()

    if (args.contains("--gui")) { // sbt "run --gui" to start with gui
      //start ui
      MachiKoroApp.controller = controller
      MachiKoroApp.startGamestate = startGamestate
      MachiKoroApp.main(args)
    }else{
      val gui = new TUI(controller)
      //controller.winCondition = _.hasWonTheGame() //Pattern Strategy the condition on which a player wins the game could also be _.money >= 50
      controller.startTurn(startGamestate)
    }

  }
}
