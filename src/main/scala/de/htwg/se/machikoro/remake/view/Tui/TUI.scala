package de.htwg.se.machikoro.remake.view.Tui

import de.htwg.se.machikoro.remake.controller.*
import de.htwg.se.machikoro.remake.model.*


class TUI extends viewObserver {

  def update(gamestate: Gamestate): Unit = {
    draw(gamestate)
    handleInput(gamestate)
  }

  def draw(gamestate: Gamestate): Unit = {
    //yooo have to do that
  }
//StartofTurn, ChooseDiceAmount,Result1,AskForRejectionOfResult,Result2,Cardeffects,Buyphase,PlayerWins
  def handleVisuals(gamestate: Gamestate): Unit = gamestate.state match {
    case turnState.StartofTurn => gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).foreach(p => p.printAllCards())
    case turnState.Result1 => println(gamestate.DiceResult)
    case turnState.Result2 => println(gamestate.DiceResult) 
    case turnState.PlayerWins => println("Player " + (gamestate.CurrentTurnPlayerId +1) + " won!" )
    case _ => // do nothing
  }

  def handleInput(gamestate: Gamestate): Unit = gamestate.state match {
    case turnState.ChooseDiceAmount =>
      val dice = getDiceAmount()
      Controller.handleInput(ChooseDiceAmount(dice))

    case turnState.Buyphase =>
      val cardName = getCardToBuy()
      Controller.handleInput(BuyCard(cardName))

    case turnState.AskForRejectionOfResult =>
      val reject = askForRejection()
      Controller.handleInput(RejectDiceRoll(reject))

    case _ => // do nothing
  }

  private def getDiceAmount(): Int = {
    val input = scala.io.StdIn.readLine("How many dice? (1/2): ")
    input match {
      case "1" => 1
      case "2" => 2
      case _ =>
        println("Invalid input")
        getDiceAmount()
    }
  }

  private def getCardToBuy(): String =
    scala.io.StdIn.readLine("Enter card name to buy (or 'next'): ")

  private def askForRejection(): Boolean =
    scala.io.StdIn.readLine("Reject dice? (y/n): ").toLowerCase match {
      case "y" => true
      case "n" => false
      case _ =>
        println("Invalid input")
        askForRejection()
    }
}