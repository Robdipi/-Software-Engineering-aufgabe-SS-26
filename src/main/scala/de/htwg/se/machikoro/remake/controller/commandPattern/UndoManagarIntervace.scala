package de.htwg.se.machikoro.remake.controller.commandPattern

import de.htwg.se.machikoro.remake.controller.mementoPatern.mementoIntervace
import de.htwg.se.machikoro.remake.model.Data.Gamestate

trait UndoManagerInterface {
  def doStep(gamestate: Gamestate, command: Command): Unit

  def undoStep(gamestate: Gamestate, n: Int): Unit

  def delete(mementoName: String): Unit

  def loadSavefiles(mementos: List[mementoIntervace]): Option[Gamestate]
}


trait Command(val savedGamestate: mementoIntervace) {
  def doStep(gamestate: Gamestate):Unit
  def undoStep(gamestate: Gamestate):Unit
  def redoStep(gamestate: Gamestate):Unit
}
