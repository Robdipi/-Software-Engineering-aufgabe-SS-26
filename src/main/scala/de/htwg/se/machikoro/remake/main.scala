package de.htwg.se.machikoro.remake

import com.google.inject.Guice
import de.htwg.se.machikoro.remake.controller.commandPattern.UndoManagerInterface
import de.htwg.se.machikoro.remake.controller.main.{ControllerInterface, WinCondition}
import de.htwg.se.machikoro.remake.controller.main.impl1.ControllerV2
import de.htwg.se.machikoro.remake.controller.mementoPatern.mementoCreator
import de.htwg.se.machikoro.remake.model.Gamestate
import de.htwg.se.machikoro.remake.model.initialization.gameInitializationSystem
import de.htwg.se.machikoro.remake.view.{ViewInterface, starterInterface}
import scalafx.application.JFXApp3



object main{
  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(new AppModule(args))
    
    val GameInitializationSystem: gameInitializationSystem = injector.getInstance(classOf[gameInitializationSystem])
    var startGamestate = GameInitializationSystem(2, "standart") //hell_of_weat
    
    val controller = injector.getInstance(classOf[ControllerInterface])
    val undoManager = injector.getInstance(classOf[UndoManagerInterface])

    if (args.contains("--mem")) {// loads the last save that was left in the old saves and then deletes the old saves
      startGamestate =
        mementoCreator.loadGamesave(undoManager).getOrElse(startGamestate)    
    }
    mementoCreator.flushSavefiles()


    val ui = injector.getInstance(classOf[ViewInterface])
    controller.add(ui)
    if (args.contains("--gui")) { // sbt "run --gui" to start with gui
      val starter = injector.getInstance(classOf[starterInterface])
      starter.controller = controller
      starter.startGamestate = startGamestate
      starter.main(args)
    }else{
      controller.startTurn(startGamestate)
    }

  }
}
