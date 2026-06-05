package de.htwg.se.machikoro.remake.controller

import de.htwg.se.machikoro.remake.model.Color.{Purple, Yellow}
import de.htwg.se.machikoro.remake.model.turnState.*
import de.htwg.se.machikoro.remake.model.*




sealed trait UserInput
case class ChooseDiceAmount(amount: Int) extends UserInput
case class BuyCard(cardName: String) extends UserInput
case class RejectDiceRoll(reject: Boolean) extends UserInput

class ControllerV2(val winCondition: Player => Boolean ) extends viewObserverable {

 // var gamestate = Gamestate()
  private var rndManager = RandomnessManager()
  private val undoManager = new UndoManager()
 



  /*

  Input

   */

  def handleInput(input: UserInput, gamestate: Gamestate): Unit = input match {
    case ChooseDiceAmount(amount) =>
      undoManager.doStep(gamestate,new ChooseDiceCommand(amount,gamestate))

    case BuyCard(cardName) =>
      undoManager.doStep(gamestate,new BuyCardCommand(cardName,gamestate))

    case RejectDiceRoll(reject) =>
      undoManager.doStep(gamestate,new RejectDiceCommand(reject,gamestate))
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

  class ChooseDiceCommand(amount: Int, val previousState: Gamestate) extends Command {
    override def doStep(gamestate: Gamestate): Unit = {
      val gamestate2 = gamestate.changeDiceChosen(amount).changeState(Result1)
      notifiyObservers(gamestate2)
      resultone(gamestate2)
    }
    override def undoStep(gamestate: Gamestate): Unit = {
      startTurn(previousState)
    }
    override def redoStep(gamestate: Gamestate): Unit = {
      doStep(previousState)
    }
  }



  class BuyCardCommand(cardName: String,val previousState: Gamestate) extends Command {

    override def doStep(gamestate: Gamestate): Unit = {

      val input = cardName
      if (input == "next") endOfTurn(gamestate)

      gamestate.cardStacks.find(_.stackCard.cardName == input) match {
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
          tryToBuy(gamestate.changeState(NONE_EXISTANT_CARDNAME_WARNING))

      }
    }
    override def undoStep(gamestate: Gamestate): Unit = {
      startTurn(previousState)
    }
    override def redoStep(gamestate: Gamestate): Unit = {
      doStep(previousState)
    }
  }

    class RejectDiceCommand(reject: Boolean, val previousState: Gamestate) extends Command {

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
        startTurn(previousState)
      }

      override def redoStep(gamestate: Gamestate): Unit = {
        doStep(previousState)
      }
    }
  //----------------------------------------------------------------------
  }
