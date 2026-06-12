package de.htwg.se.machikoro.remake.controller.commandPattern.impl1

import de.htwg.se.machikoro.remake.controller.commandPattern.{Command, UndoManagerInterface}
import de.htwg.se.machikoro.remake.controller.mementoPatern.{mementoConstatants, mementoIntervace}
import de.htwg.se.machikoro.remake.model.Gamestate



  
class UndoManager extends UndoManagerInterface {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(gamestate: Gamestate, command: Command) = {
    undoStack = command :: undoStack
    if (undoStack.length>= mementoConstatants.MAXIMUM_SAFEFILES) {
      delete(undoStack.last.savedGamestate.safeFilePath)  // deletes the last savefile if they get to many not the most efficent way but Id doesn't really matter
    }
    command.doStep(gamestate)
  }

  def undoStep(gamestate: Gamestate, n: Int) = {
    for (_ <- 1 to n){
      undoStack match {
        case Nil =>
        case head :: stack => {
          head.undoStep(gamestate)
          undoStack = stack
          redoStack = head :: redoStack
        }
      }
    }
  }
  def delete(mementoName : String) = {
    undoStack = undoStack.filterNot(_.savedGamestate.safeFilePath.equals(mementoName))
  }

  def loadSavefiles(mementos: List[mementoIntervace]): Option[Gamestate] = {
    mementos.headOption.flatMap(_.restore())
  }
}