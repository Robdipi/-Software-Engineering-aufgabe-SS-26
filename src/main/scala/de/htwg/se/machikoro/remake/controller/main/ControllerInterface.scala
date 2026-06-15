package de.htwg.se.machikoro.remake.controller.main

import de.htwg.se.machikoro.remake.controller.commandPattern.{Command, UndoManagerInterface}
import de.htwg.se.machikoro.remake.controller.main.impl1.RandomnessManager
import de.htwg.se.machikoro.remake.controller.mementoPatern.mementoIntervace
import de.htwg.se.machikoro.remake.model.Data.{Gamestate, Player}


trait UserInput
case class ChooseDiceAmountInput(amount: Int) extends UserInput
case class BuyCardInput(cardName: String) extends UserInput
case class RejectDiceRollInput(reject: Boolean) extends UserInput

trait ControllerInterface extends ViewObservable {
 
  def handleInput(input: UserInput, gamestate: Gamestate): Unit
  def startTurn(gamestate: Gamestate): Unit
}

trait WinCondition:
  def check(player: Player): Boolean