package de.htwg.se.machikoro.remake

import com.google.inject.AbstractModule
import de.htwg.se.machikoro.remake.controller.commandPattern.UndoManagerInterface
import de.htwg.se.machikoro.remake.controller.commandPattern.impl1.UndoManager
import de.htwg.se.machikoro.remake.controller.main.{ControllerInterface, WinCondition}
import de.htwg.se.machikoro.remake.controller.main.impl1.{ControllerV2, DefaultWinCondition, minimalWinCondition}
import de.htwg.se.machikoro.remake.controller.mementoPatern.mementoIntervace
import de.htwg.se.machikoro.remake.controller.mementoPatern.implJson.*
import de.htwg.se.machikoro.remake.controller.mementoPatern.implXml.*
import de.htwg.se.machikoro.remake.model.initialization.gameInitializationSystem
import de.htwg.se.machikoro.remake.model.initialization.impl1.*
import de.htwg.se.machikoro.remake.view.Gui.{GUI, MachiKoroApp}
import de.htwg.se.machikoro.remake.view.Tui.TUI
import de.htwg.se.machikoro.remake.view.{ViewInterface, starterInterface}


class AppModule(args : Array[String]) extends AbstractModule {
  override def configure(): Unit = {
  
    //model/initialization component
    bind(classOf[gameInitializationSystem]).to(classOf[Game])
    
    //---------------------------------------------------------------------------------

    //controller/commandPattern component
    bind(classOf[UndoManagerInterface]).to(classOf[UndoManager])


    //controller/mementoPattern component
    if (args.contains("--xml")) {
      bind(classOf[mementoIntervace]).to(classOf[mementoXml])
    } else {
      bind(classOf[mementoIntervace]).to(classOf[mementoJson])
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
    bind(classOf[starterInterface]).to(classOf[MachiKoroApp])

  }


}
 