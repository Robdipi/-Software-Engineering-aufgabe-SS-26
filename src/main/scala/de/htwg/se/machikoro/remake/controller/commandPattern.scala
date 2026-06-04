package de.htwg.se.machikoro.remake.controller

import de.htwg.se.machikoro.remake.model.Gamestate


trait Command {
  def doStep(gamestate: Gamestate):Unit
  def undoStep(gamestate: Gamestate):Unit
  def redoStep(gamestate: Gamestate):Unit
}


class UndoManager {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(gamestate: Gamestate, command: Command) = {
    undoStack = command :: undoStack
    command.doStep(gamestate)
  }

  def undoStep(gamestate: Gamestate) = {
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