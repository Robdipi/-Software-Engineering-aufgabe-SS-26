package de.htwg.se.machikoro.remake.controller.main.impl1

import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.commandPattern.{Command, UndoManagerInterface}
import de.htwg.se.machikoro.remake.controller.main.{BuyCardInput, ChooseDiceAmountInput, ControllerInterface, RejectDiceRollInput, UserInput, WinCondition}
import de.htwg.se.machikoro.remake.controller.mementoPatern.{MementoCareTakerInterface, MementoIntervace}
import de.htwg.se.machikoro.remake.model.Data.Color.{Purple, Yellow}
import de.htwg.se.machikoro.remake.model.Data.TurnState.*
import de.htwg.se.machikoro.remake.model.Data.{Gamestate, Player, TurnState}

class DefaultWinCondition extends WinCondition{
  def check(player: Player): Boolean = {
    player.hasWonTheGame()
  }
}
class minimalWinCondition extends WinCondition{
  def check(player: Player): Boolean = {
    player.hasWonTheGameSmallRound()
  }
}





class ControllerV2 @Inject() (val winCondition: WinCondition, 
                              val undoManager: UndoManagerInterface,
                              val mementoCreator : MementoCareTakerInterface) extends ControllerInterface {
  private var rndManager = RandomnessManager()
  


  //----------------------------------------------------------------------


  def handleInput(input: UserInput, gamestate: Gamestate): Unit = input match {
    case ChooseDiceAmountInput(amount) =>
      undoManager.doStep(gamestate,new ChooseDiceCommand(amount,mementoCreator.create(gamestate,undoManager)))

    case BuyCardInput(cardName) =>
      undoManager.doStep(gamestate,new BuyCardCommand(cardName,mementoCreator.create(gamestate,undoManager)))

    case RejectDiceRollInput(reject) =>
      undoManager.doStep(gamestate,new RejectDiceCommand(reject,mementoCreator.create(gamestate,undoManager)))
  }


  //----------------------------------------------------------------------


  private def tryToBuy(gamestate: Gamestate): Unit = {
    notifyObservers(gamestate.changeState(Buyphase))
  }

  /** Shows a purchase warning before returning to the buy phase.
    * Keeping the warning as a separate observer event lets TUI and GUI display it,
    * while the following Buyphase event keeps the current player in the purchase flow.
    */
  private def warnAndRetryBuy(gamestate: Gamestate): Unit = {
    notifyObservers(gamestate)
    tryToBuy(gamestate)
  }

  def startTurn(gamestate: Gamestate): Unit = {
    val gamestate1 = gamestate.changeState(TurnState.StartofTurn)
    notifyObservers(gamestate1)
    if (gamestate1.Players.find(_.playerId == gamestate1.CurrentTurnPlayerId).exists(_.canChooseDyeAmount())) {
      notifyObservers(gamestate1.changeState(TurnState.ChooseDiceAmount))
    } else {
      resultone(gamestate1.changeState(TurnState.Result1).changeDiceChosen(1))
    }
  }

  private def endOfTurn(gamestate: Gamestate): Unit = {
    if (gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(winCondition.check)) {
      notifyObservers(gamestate.changeState(PlayerWins))

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

    notifyObservers(newState)

    if (state.Players.exists(p => p.playerId == state.CurrentTurnPlayerId && p.canRejectDyeTrow())) {
      notifyObservers(newState.changeState(TurnState.AskForRejectionOfResult))
    } else {
      activateCardsController(newState)
    }
  }



  private def activateCardsController(state: Gamestate): Unit = {
    val s2 = state.activateCards(state.DiceResult, state.CurrentTurnPlayerId).changeState(Result2)
    notifyObservers(s2)
    tryToBuy(s2)
  }


  //----------------------------------------------------------------------

   private class ChooseDiceCommand(amount: Int, savedGamestate : MementoIntervace) extends Command(savedGamestate){
    override def doStep(gamestate: Gamestate): Unit = {
      val gamestate2 = gamestate.changeDiceChosen(amount).changeState(Result1)
      notifyObservers(gamestate2)
      resultone(gamestate2)
    }
    override def undoStep(gamestate: Gamestate): Unit = {
      startTurn(savedGamestate.restore().getOrElse(gamestate))
    }
    override def redoStep(gamestate: Gamestate): Unit = {
      doStep(savedGamestate.restore().getOrElse(gamestate))
    }
  }



  private class BuyCardCommand(cardName: String, savedGamestate : MementoIntervace) extends Command(savedGamestate) {

    override def doStep(gamestate: Gamestate): Unit = {
      val input = cardName.trim

      if (input == "next") {
        endOfTurn(gamestate)
      } else {
        gamestate.cardStacks.find(_.stackCard.cardName.equalsIgnoreCase(input)) match {
          case Some(stack) =>
            gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId) match {
              case None =>
                warnAndRetryBuy(gamestate.changeState(NONE_EXISTANT_CARDNAME_WARNING))
              case Some(currentPlayer) =>
                val card = stack.stackCard
                if (currentPlayer.money < card.price) {
                  warnAndRetryBuy(gamestate.changeState(YOU_CANT_AFFORD_THIS_WARNING))
                } else if (card.color == Yellow && currentPlayer.properties.exists(_.cardName == card.cardName)) {
                  warnAndRetryBuy(gamestate.changeState(ALREADY_OWN_THAT_YELLOW_CARD_WARNING))
                } else if (card.color == Purple && currentPlayer.properties.exists(_.color == Purple)) {
                  warnAndRetryBuy(gamestate.changeState(ALREADY_OWN_PURPLE_CARD_WARNING))
                } else if (stack.amount <= 0) {
                  warnAndRetryBuy(gamestate.changeState(NO_CARDS_LEFT_OF_THAT_TYPE_WARNING))
                } else {
                  endOfTurn(gamestate.changeMoneyOfPlayer(gamestate.CurrentTurnPlayerId, -card.price)
                    .removeCardFromStack(card)
                    .giveCard(gamestate.CurrentTurnPlayerId, card))
                }
            }
          case None if input.matches("undo\\d+") =>
            undoManager.undoStep(gamestate, input.stripPrefix("undo").toInt)
          case None =>
            warnAndRetryBuy(gamestate.changeState(NONE_EXISTANT_CARDNAME_WARNING))
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

    private class RejectDiceCommand(reject: Boolean, savedGamestate : MementoIntervace) extends Command(savedGamestate) {

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
