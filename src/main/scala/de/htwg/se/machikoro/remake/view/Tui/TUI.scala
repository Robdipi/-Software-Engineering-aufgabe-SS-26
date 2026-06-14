package de.htwg.se.machikoro.remake.view.Tui

import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.main.{BuyCardInput, ChooseDiceAmountInput, ControllerInterface, RejectDiceRollInput, viewObserver}
import de.htwg.se.machikoro.remake.model.{Gamestate, turnState}
import de.htwg.se.machikoro.remake.model.turnState.{Buyphase, Cardeffects}
import de.htwg.se.machikoro.remake.view.ViewInterface


class TUI @Inject() (controller: ControllerInterface)  extends ViewInterface {
  var inputManager : InputManager = new InputManager()
  controller.add(this)
  
  def update(gamestate: Gamestate): Unit = {
    println(s"UPDATE: ${gamestate.state}")//To debug
    handleVisuals(gamestate)
    handleInput(gamestate)
  }


//StartofTurn, ChooseDiceAmount,Result1,AskForRejectionOfResult,Result2,Cardeffects,Buyphase,PlayerWins
  def handleVisuals(gamestate: Gamestate): Unit = gamestate.state match {
    case turnState.StartofTurn =>
      println("---------------------------------------------------------------------" )
      println("Player " + (gamestate.CurrentTurnPlayerId +1) + " turn" )
      gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).foreach(p => p.printAllCards())//all cards of current player
    case turnState.Result1 => println(gamestate.DiceResult)
    case turnState.Result2 => println(gamestate.DiceResult)
    case turnState.PlayerWins => println("Player " + (gamestate.CurrentTurnPlayerId +1) + " won!" )
    case turnState.ALREADY_OWN_THAT_YELLOW_CARD_WARNING => println("You already own this yellow card")
    case turnState.ALREADY_OWN_PURPLE_CARD_WARNING => println("You already own a purple card")
    case turnState.NO_CARDS_LEFT_OF_THAT_TYPE_WARNING => println("No cards left of this type")
    case turnState.YOU_CANT_AFFORD_THIS_WARNING => println("You don't have enought money")
    case turnState.NONE_EXISTANT_CARDNAME_WARNING => println("Cardname doesn't exist")
    case _ => // do nothing
  }



//dont change stuff bellow here
  def handleInput(gamestate: Gamestate): Unit = gamestate.state match {
    case turnState.ChooseDiceAmount =>
      val dice = getDiceAmount()
      controller.handleInput(ChooseDiceAmountInput(dice), gamestate)
    case turnState.Buyphase =>
      println("")
      println("These Cards are currently available to buy: ")
      println("")
      gamestate.cardStacks.foreach(cardStack => println(s"${cardStack.stackCard.price}€-${cardStack.stackCard.cardName} x${cardStack.amount}"))
      println("")
      val money = gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).map(_.money).getOrElse(0)
      println(s"You have $money €")

      val cardName = getCardToBuy()
      controller.handleInput(BuyCardInput(cardName), gamestate)
    case turnState.AskForRejectionOfResult =>
      val reject = askForRejection()
      controller.handleInput(RejectDiceRollInput(reject), gamestate)
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

  private def getCardToBuy(): String = {
    val (input, inputManager2) = inputManager.getNextInput("Enter card name to buy (or 'next'): ")
    inputManager = inputManager2
    return input
  }



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