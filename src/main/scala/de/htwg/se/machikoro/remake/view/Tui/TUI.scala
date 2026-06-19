package de.htwg.se.machikoro.remake.view.Tui

import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.main.{BuyCardInput, ChooseDiceAmountInput, ControllerInterface, RejectDiceRollInput}
import de.htwg.se.machikoro.remake.model.Data.{Gamestate, TurnState}
import de.htwg.se.machikoro.remake.view.ViewInterface

import scala.annotation.tailrec


class TUI @Inject() (controller: ControllerInterface)  extends ViewInterface {
  var inputManager : InputManager = InputManager()
  controller.add(this)
  
  def update(gamestate: Gamestate): Unit = {
    println(s"UPDATE: ${gamestate.state}")//To debug
    handleVisuals(gamestate)
    handleInput(gamestate)
  }


//StartofTurn, ChooseDiceAmount,Result1,AskForRejectionOfResult,Result2,Cardeffects,Buyphase,PlayerWins
  private def handleVisuals(gamestate: Gamestate): Unit = gamestate.state match {
    case TurnState.StartofTurn =>
      println("---------------------------------------------------------------------" )
      println("Player " + (gamestate.CurrentTurnPlayerId +1) + " turn" )
      gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).foreach(p => p.printAllCards())//all cards of current player
    case TurnState.Result1 => println(gamestate.DiceResult)
    case TurnState.Result2 => println(gamestate.DiceResult)
    case TurnState.PlayerWins => println("Player " + (gamestate.CurrentTurnPlayerId +1) + " won!" )
    case TurnState.ALREADY_OWN_THAT_YELLOW_CARD_WARNING => println("You already own this yellow card")
    case TurnState.ALREADY_OWN_PURPLE_CARD_WARNING => println("You already own a purple card")
    case TurnState.NO_CARDS_LEFT_OF_THAT_TYPE_WARNING => println("No cards left of this type")
    case TurnState.YOU_CANT_AFFORD_THIS_WARNING => println("You don't have enough money")
    case TurnState.NONE_EXISTANT_CARDNAME_WARNING => println("Card name doesn't exist")
    case _ => // do nothing
  }



//dont change stuff bellow here
  def handleInput(gamestate: Gamestate): Unit = gamestate.state match {
    case TurnState.ChooseDiceAmount =>
      val dice = getDiceAmount()
      controller.handleInput(ChooseDiceAmountInput(dice), gamestate)
    case TurnState.Buyphase =>
      println("")
      println("These Cards are currently available to buy: ")
      println("")
      gamestate.cardStacks.foreach(cardStack => println(s"${cardStack.stackCard.price}€-${cardStack.stackCard.cardName} x${cardStack.amount}"))
      println("")
      val money = gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).map(_.money).getOrElse(0)
      println(s"You have $money €")

      val cardName = getCardToBuy()
      controller.handleInput(BuyCardInput(cardName), gamestate)
    case TurnState.AskForRejectionOfResult =>
      val reject = askForRejection()
      controller.handleInput(RejectDiceRollInput(reject), gamestate)
    case _ => // do nothing
  }

  @tailrec
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

  private def getCardToBuy(): String = {
    val (input, inputManager2) = inputManager.getNextInput("Enter card name to buy (or 'next'): ")
    inputManager = inputManager2
    input
  }



  @tailrec
  private def askForRejection(): Boolean = {
    val (input, inputManager2) = inputManager.getNextInput("Reject dice? (y/n): ")
    inputManager = inputManager2
    input.toLowerCase match {
      case "y" => true
      case "n" => false
      case _ =>
        println("Invalid input")
        askForRejection()
    }
  }
}