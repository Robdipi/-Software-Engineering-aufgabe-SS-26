package de.htwg.se.machikoro.remake

import de.htwg.se.machikoro.remake.controller.main.ControllerV2
import de.htwg.se.machikoro.remake.controller.mementoPatern.mementoCreator
import de.htwg.se.machikoro.remake.model.initialization.{Game, gameInitializationSystem}
import de.htwg.se.machikoro.remake.view.Gui.MachiKoroApp
import de.htwg.se.machikoro.remake.view.Tui.TUI

object main {
  def main(args: Array[String]): Unit = {
    val gameInitializationSystem: gameInitializationSystem = new Game()
    var startGamestate = gameInitializationSystem(2, "standart")
    val controller = new ControllerV2(_.hasWonTheGame())

    if args.contains("--mem") then
      startGamestate = mementoCreator.loadGamesave(controller.undoManager).getOrElse(startGamestate)

    mementoCreator.flushSavefiles()

    if args.contains("--gui") then
      MachiKoroApp.controller = controller
      MachiKoroApp.startGamestate = startGamestate
      MachiKoroApp.main(args)
    else
      new TUI(controller)
      controller.startTurn(startGamestate)
  }
}
