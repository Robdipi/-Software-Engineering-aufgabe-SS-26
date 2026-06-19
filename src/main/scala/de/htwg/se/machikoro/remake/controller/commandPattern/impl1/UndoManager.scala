package de.htwg.se.machikoro.remake.controller.commandPattern.impl1

import de.htwg.se.machikoro.remake.controller.commandPattern.{Command, UndoManagerInterface}
import de.htwg.se.machikoro.remake.controller.mementoPatern.{MementoConstatants, MementoIntervace}
import de.htwg.se.machikoro.remake.model.Data.Gamestate


class UndoManager extends UndoManagerInterface {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil



  // deletes the last savefile if they get to many not the most efficient way, but it doesn't really matter
  def doStep(gamestate: Gamestate, command: Command): Unit = {
    undoStack = command :: undoStack
    if (undoStack.length>= MementoConstatants.MAXIMUM_SAFEFILES) {
      delete(undoStack.last.savedGamestate.safeFilePath)  
    }
    command.doStep(gamestate)
  }

  def undoStep(gamestate: Gamestate, n: Int): Unit = {
    for (_ <- 1 to n){
      undoStack match {
        case Nil =>
        case head :: stack => 
          head.undoStep(gamestate)
          undoStack = stack
          redoStack = head :: redoStack
        
      }
    }
  }
  def delete(mementoName : String): Unit = {
    undoStack = undoStack.filterNot(_.savedGamestate.safeFilePath.equals(mementoName))
  }

  def loadSavefiles(mementos: List[MementoIntervace]): Option[Gamestate] = {
    mementos.headOption.flatMap(_.restore())
  }
}