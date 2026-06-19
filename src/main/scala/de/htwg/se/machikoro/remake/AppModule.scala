package de.htwg.se.machikoro.remake

import com.google.inject.AbstractModule
import de.htwg.se.machikoro.remake.controller.commandPattern.UndoManagerInterface
import de.htwg.se.machikoro.remake.controller.commandPattern.impl1.UndoManager
import de.htwg.se.machikoro.remake.controller.main.{ControllerInterface, WinCondition}
import de.htwg.se.machikoro.remake.controller.main.impl1.{ControllerV2, DefaultWinCondition, minimalWinCondition}
import de.htwg.se.machikoro.remake.controller.mementoPatern.Memento.implJson.{MementoCreatorJson, MementoJson}
import de.htwg.se.machikoro.remake.controller.mementoPatern.Memento.implXml.{MementoCareTakerXml, MementoXml}
import de.htwg.se.machikoro.remake.controller.mementoPatern.{MementoCareTakerInterface, MementoIntervace}
import de.htwg.se.machikoro.remake.model.initialization.GameInitializationSystem
import de.htwg.se.machikoro.remake.model.initialization.impl1.*
import de.htwg.se.machikoro.remake.view.Gui.{GUI, MachiKoroApp}
import de.htwg.se.machikoro.remake.view.Tui.TUI
import de.htwg.se.machikoro.remake.view.{ViewInterface, StarterInterface}


class AppModule(args : Array[String]) extends AbstractModule {
  override def configure(): Unit = {
  
    //model/initialization component
    bind(classOf[GameInitializationSystem]).to(classOf[Game])
    
    //---------------------------------------------------------------------------------

    //controller/commandPattern component
    bind(classOf[UndoManagerInterface]).to(classOf[UndoManager])


    //controller/mementoPattern component        // sbt "run --xml" to start with using xml for saving
    if (args.contains("--xml")) {
      println("using xml")
      bind(classOf[MementoIntervace]).to(classOf[MementoXml])
      bind(classOf[MementoCareTakerInterface]).to(classOf[MementoCareTakerXml])
    } else {
      println("using json")
      bind(classOf[MementoIntervace]).to(classOf[MementoJson])
      bind(classOf[MementoCareTakerInterface]).to(classOf[MementoCreatorJson])
    }
    
    //controller/main component
    bind(classOf[ControllerInterface]).to(classOf[ControllerV2])

    //short round flag
    if (args.contains("--SR")) {//
      bind(classOf[WinCondition]).to(classOf[minimalWinCondition])

    }else {
      bind(classOf[WinCondition]).to(classOf[DefaultWinCondition])
    }

    //---------------------------------------------------------------------------------

    //view/ ViewInterface Component
    if (args.contains("--gui")) {
      bind(classOf[ViewInterface]).to(classOf[GUI])
    } else {
      bind(classOf[ViewInterface]).to(classOf[TUI])
    }

    //view/starterInterface Component
    bind(classOf[StarterInterface]).to(classOf[MachiKoroApp])

  }


}
 