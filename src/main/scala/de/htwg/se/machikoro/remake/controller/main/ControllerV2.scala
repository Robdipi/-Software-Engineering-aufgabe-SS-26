package de.htwg.se.machikoro.remake.controller.main

import de.htwg.se.machikoro.remake.controller.commandPattern.{Command, UndoManagerInterface}
import de.htwg.se.machikoro.remake.controller.*
import de.htwg.se.machikoro.remake.controller.commandPattern.impl1.UndoManager
import de.htwg.se.machikoro.remake.controller.mementoPatern.implJson.mementoJson
import de.htwg.se.machikoro.remake.controller.mementoPatern.mementoCreator
import de.htwg.se.machikoro.remake.model.*
import de.htwg.se.machikoro.remake.model.Color.{Purple, Yellow}
import de.htwg.se.machikoro.remake.model.turnState.*




sealed trait UserInput
case class ChooseDiceAmount(amount: Int) extends UserInput
case class BuyCard(cardName: String) extends UserInput
case class RejectDiceRoll(reject: Boolean) extends UserInput

class ControllerV2(val winCondition: Player => Boolean ) extends viewObserverable {

 // var gamestate = Gamestate()
  private var rndManager = RandomnessManager()
  val undoManager = new UndoManager()
 



  /*

  Input

   */

  def handleInput(input: UserInput, gamestate: Gamestate): Unit = input match {
    case ChooseDiceAmount(amount) =>

      undoManager.doStep(gamestate,new ChooseDiceCommand(amount,mementoCreator.create(gamestate,Some(undoManager))))

    case BuyCard(cardName) =>
      undoManager.doStep(gamestate,new BuyCardCommand(cardName,mementoCreator.create(gamestate,Some(undoManager))))

    case RejectDiceRoll(reject) =>
      undoManager.doStep(gamestate,new RejectDiceCommand(reject,mementoCreator.create(gamestate,Some(undoManager))))
  }

  /*
  Helper methods



   */



  def tryToBuy(gamestate: Gamestate): Unit = {
    notifiyObservers(gamestate.changeState(Buyphase))
  }

  def startTurn(gamestate: Gamestate): Unit = {
    val gamestate1 = gamestate.changeState(turnState.StartofTurn)
    notifiyObservers(gamestate1)
    if (gamestate1.Players.find(_.playerId == gamestate1.CurrentTurnPlayerId).exists(_.canChooseDyeAmount())) {
      notifiyObservers(gamestate1.changeState(turnState.ChooseDiceAmount))
    } else {
      resultone(gamestate1.changeState(turnState.Result1).changeDiceChosen(1))
    }
  }

  def endOfTurn(gamestate: Gamestate): Unit = {
    if (gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(winCondition)) {
      notifiyObservers(gamestate.changeState(PlayerWins))
      System.exit(0)
    } else {
      startTurn(gamestate.iterateTurn().changeState(StartofTurn))
    }
  }
  private def resultone(state: Gamestate): Unit = {
    val (dicethrowA, rndManager1) = rndManager.getNextNum
    rndManager = rndManager1
    val (dicethrowB, rndManager2) = rndManager1.getNextNum
    rndManager = rndManager2

    val updatedPlayers = state.Players.map { currentplayer =>
      if (currentplayer.playerId == state.CurrentTurnPlayerId &&
        dicethrowA == dicethrowB &&
        state.diceChoosen == 2 &&
        currentplayer.canGetAnotherTurn())
        currentplayer.copy(GetsAnotherTurn = true)
      else currentplayer
    }

    val newState = state
      .changePlayers(updatedPlayers)
      .changeDiceResult(if (state.diceChoosen == 2) dicethrowA + dicethrowB else dicethrowA)

    notifiyObservers(newState)

    if (state.Players.exists(p => p.playerId == state.CurrentTurnPlayerId && p.canRejectDyeTrow())) {
      notifiyObservers(newState.changeState(turnState.AskForRejectionOfResult))
    } else {
      activateCardsController(newState)
    }
  }



  def activateCardsController(state: Gamestate): Unit = {
    val s2 = state.activateCards(state.DiceResult, state.CurrentTurnPlayerId).changeState(Result2)
    notifiyObservers(s2)
    tryToBuy(s2)
  }


  //----------------------------------------------------------------------

  class ChooseDiceCommand(amount: Int, savedGamestate : mementoJson) extends Command(savedGamestate){
    override def doStep(gamestate: Gamestate): Unit = {
      val gamestate2 = gamestate.changeDiceChosen(amount).changeState(Result1)
      notifiyObservers(gamestate2)
      resultone(gamestate2)
    }
    override def undoStep(gamestate: Gamestate): Unit = {
      startTurn(savedGamestate.restore().getOrElse(gamestate))
    }
    override def redoStep(gamestate: Gamestate): Unit = {
      doStep(savedGamestate.restore().getOrElse(gamestate))
    }
  }



  class BuyCardCommand(cardName: String,savedGamestate : mementoJson) extends Command(savedGamestate) {

    override def doStep(gamestate: Gamestate): Unit = {

      val input = cardName.trim

      // Wichtig: "next" beendet den Zug und darf danach NICHT weiter als Kartenname
      // gesucht werden. Sonst wird direkt danach NONE_EXISTANT_CARDNAME_WARNING
      // ausgelöst und die GUI springt scheinbar zurück in dieselbe Kaufphase.
      if (input == "next") {
        endOfTurn(gamestate)
        return
      }

      gamestate.cardStacks.find(_.stackCard.cardName.equalsIgnoreCase(input)) match {
        case Some(stack) =>
          val currentPlayer = gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).get
          val card = stack.stackCard

          if (currentPlayer.money < card.price) {
            tryToBuy(gamestate.changeState(YOU_CANT_AFFORD_THIS_WARNING))
          } else if (card.color == Yellow && currentPlayer.properties.exists(_.cardName == card.cardName)) {
            tryToBuy(gamestate.changeState(ALREADY_OWN_THAT_YELLOW_CARD_WARNING))
          } else if (card.color == Purple && currentPlayer.properties.exists(_.color == Purple)) {
            tryToBuy(gamestate.changeState(ALREADY_OWN_PURPLE_CARD_WARNING))
          } else if (stack.amount <= 0) {
            tryToBuy(gamestate.changeState(NO_CARDS_LEFT_OF_THAT_TYPE_WARNING))
          } else {
            endOfTurn(gamestate.changeMoneyOfPlayer(gamestate.CurrentTurnPlayerId, -card.price)
              .removeCardFromStack(card)
              .giveCard(gamestate.CurrentTurnPlayerId, card)
              )
          }
        case None =>
          if (cardName.contains("undo")) {    //like undo5 = 5 X undo
            val lastDigit = cardName.last.asDigit
             undoManager.undoStep(gamestate,lastDigit)
          }else{
            tryToBuy(gamestate.changeState(NONE_EXISTANT_CARDNAME_WARNING))
          }
      }
    }
    override def undoStep(gamestate: Gamestate): Unit = {
      startTurn(savedGamestate.restore().getOrElse(gamestate))
    }
    override def redoStep(gamestate: Gamestate): Unit = {
      doStep(savedGamestate.restore().getOrElse(gamestate))
    }
  }

    class RejectDiceCommand(reject: Boolean,savedGamestate : mementoJson) extends Command(savedGamestate) {

      override def doStep(gamestate: Gamestate): Unit = {
        if (reject) {
          val (dicethrowA, rndManager1) = rndManager.getNextNum
          rndManager = rndManager1
          val (dicethrowB, rndManager2) = rndManager1.getNextNum
          rndManager = rndManager2

          val gamestate2 = gamestate.changeDiceResult(if (gamestate.diceChoosen == 2) dicethrowA + dicethrowB else dicethrowA)
            .changeState(Result2)

          activateCardsController(gamestate2)
        } else {
          activateCardsController(gamestate)
        }
      }

      override def undoStep(gamestate: Gamestate): Unit = {
        startTurn(savedGamestate.restore().getOrElse(gamestate))
      }

      override def redoStep(gamestate: Gamestate): Unit = {
        doStep(savedGamestate.restore().getOrElse(gamestate))
      }
    }
  //----------------------------------------------------------------------
  }
