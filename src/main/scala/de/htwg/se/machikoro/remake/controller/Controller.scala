package de.htwg.se.machikoro.remake.controller

import de.htwg.se.machikoro.remake.model.*



sealed trait UserInput
case class ChooseDiceAmount(amount: Int) extends UserInput
case class BuyCard(cardName: String) extends UserInput
case class RejectDiceRoll(reject: Boolean) extends UserInput

object Controller extends viewObserverable{
  var gamestate = new Gamestate()
  
  def handleInput(input: UserInput): Unit = input match {
    case ChooseDiceAmount(amount) =>
      gamestate = gamestate.copy(diceChoosen = amount, state = turnState.Result1)
      notifiyObservers(gamestate)
    case BuyCard(cardName) =>
      gamestate = processBuyingCard(gamestate, cardName)
      notifiyObservers(gamestate)
    case RejectDiceRoll(reject) =>
      gamestate = processRejection(gamestate, reject)
      notifiyObservers(gamestate)
  }
  
  
  
  def processBuyingCard(gamestate:Gamestate, cardName:String): Gamestate = {
    return gamestate
  }

  def processRejection(gamestate: Gamestate, reject: Boolean): Gamestate = {
    return gamestate
  }

}
