package de.htwg.se.machikoro.remake

import com.google.inject.Guice
import de.htwg.se.machikoro.remake.controller.commandPattern.UndoManagerInterface
import de.htwg.se.machikoro.remake.controller.main.ControllerInterface
import de.htwg.se.machikoro.remake.controller.mementoPatern.MementoCareTakerInterface
import de.htwg.se.machikoro.remake.model.initialization.GameInitializationSystem
import de.htwg.se.machikoro.remake.view.{ViewInterface, StarterInterface}



object main{
  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(new AppModule(args))
    
    val GameInitializationSystem: GameInitializationSystem = injector.getInstance(classOf[GameInitializationSystem])
    var startGamestate = GameInitializationSystem(2, "standard") //hell_of_wheat
    
    val controller = injector.getInstance(classOf[ControllerInterface])
    val undoManager = injector.getInstance(classOf[UndoManagerInterface])
    val mementoCreator = injector.getInstance(classOf[MementoCareTakerInterface])
    
    if (args.contains("--mem")) {// loads the last save that was left in the old saves and then deletes the old saves
      startGamestate = mementoCreator.loadGamesave(undoManager).getOrElse(startGamestate)    
    }
    mementoCreator.flushSavefiles()


    val ui = injector.getInstance(classOf[ViewInterface])
    controller.add(ui)
    if (args.contains("--gui")) { // sbt "run --gui" to start with gui
      val starter = injector.getInstance(classOf[StarterInterface])
      starter.startView(startGamestate)
    }else{
      controller.startTurn(startGamestate)
    }

  }
}
