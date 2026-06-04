package de.htwg.se.machikoro.remake.controller.Depreciated

import de.htwg.se.machikoro.remake.controller.viewObserverable
import de.htwg.se.machikoro.remake.model.*
import de.htwg.se.machikoro.remake.model.Color.{Purple, Yellow}
import de.htwg.se.machikoro.remake.model.turnState.*



/*

  //Im well aware Lazy Command needs more memory than a good implemented Undo Variation but I don't care
  trait LazyCommand extends Command {
    private var previousState: Gamestate = gamestate

    override def doStep: Gamestate = {
      previousState = gamestate
      return actualStep
    }

    def actualStep: Gamestate
    override def undoStep: Gamestate = previousState
    override def redoStep: Gamestate = doStep
  }
*/
@deprecated
  object Controller extends viewObserverable {
/*
    var gamestate = new Gamestate()
    var rndManager = new RandomnessManager()
    var winCondition: Player => Boolean = _.hasWonTheGameSmallRound()
    val undoManager = new UndoManager


    def handleInput(input: UserInput): Unit = input match {
      case ChooseDiceAmount(amount) =>
        gamestate = gamestate.changeDiceChosen(amount).changeState(turnState.Result1)
        resultone() //goes to resultone in the map
      case BuyCard(cardName) =>
        gamestate = processBuyingCard(gamestate, cardName)
        notifiyObservers(gamestate)
        if (gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(winCondition)) {
          gamestate = gamestate.changeState(PlayerWins)
          notifiyObservers(gamestate)
        } else {
          gamestate = gamestate.iterateTurn()
          startphase()
        }
      case RejectDiceRoll(reject) =>
        gamestate = processRejection(gamestate, reject)
        notifiyObservers(gamestate)
        activateCards()
    }

    def startphase(): Unit = {
      gamestate = gamestate.changeState(turnState.StartofTurn)
      notifiyObservers(gamestate) //starts input request
      if (gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(_.canChooseDyeAmount())) {
        gamestate = gamestate.changeState(turnState.ChooseDiceAmount)
        notifiyObservers(gamestate) //starts input request
      } else {
        gamestate = gamestate.changeState(turnState.Result1).changeDiceChosen(1)
        resultone() //goes to resultone in the map iss only made in resultone
        //no notify as the result
      }
    }

    def resultone(): Unit = {

      val (dicethrowA, rndManager1) = rndManager.getNextNum
      rndManager = rndManager1
      val (dicethrowB, rndManager2) = rndManager1.getNextNum
      rndManager = rndManager2
      if (dicethrowA == dicethrowB
        && gamestate.diceChoosen == 2
        && gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(_.canGetAnotherTurn())) { //Pasch und die Karte die einem einem Weiter Zug gibt only works on first throw
        val updatedPlayers = gamestate.Players.map { currentplayer =>
          if (currentplayer.playerId == gamestate.CurrentTurnPlayerId) {
            currentplayer.copy(GetsAnotherTurn = true)
          } else {
            currentplayer
          }
        }
        gamestate = gamestate.changePlayers(updatedPlayers)
      }

      gamestate = gamestate.changeDiceResult(if (gamestate.diceChoosen == 2) dicethrowA + dicethrowB else dicethrowA)
      notifiyObservers(gamestate)


      if (gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(_.canRejectDyeTrow())) {
        gamestate = gamestate.changeState(turnState.AskForRejectionOfResult)
        notifiyObservers(gamestate) //starts input request
      } else {
        gamestate = gamestate.changeState(Cardeffects) // goes directly to the buy
        //starts input request
        activateCards()
      }
    }

    def activateCards(): Unit = {
      gamestate = gamestate.activateCards(gamestate.DiceResult, gamestate.CurrentTurnPlayerId)
      notifiyObservers(gamestate)
      gamestate = gamestate.changeState(Buyphase)
      notifiyObservers(gamestate)
    }


    private def processBuyingCard(state: Gamestate, input: String): Gamestate = {
      if (input == "next") return state.changeState(EndofTurn)

      state.cardStacks.find(_.stackCard.cardName == input) match {
        case Some(stack) =>
          val currentPlayer = state.Players.find(_.playerId == state.CurrentTurnPlayerId).get
          val card = stack.stackCard

          if (currentPlayer.money < card.price) {
            state.changeState(YOU_CANT_AFFORD_THIS_WARNING)
          } else if (card.color == Yellow && currentPlayer.properties.exists(_.cardName == card.cardName)) {
            state.changeState(ALREADY_OWN_THAT_YELLOW_CARD_WARNING)
          } else if (card.color == Purple && currentPlayer.properties.exists(_.color == Purple)) {
            state.changeState(ALREADY_OWN_PURPLE_CARD_WARNING)
          } else if (stack.amount <= 0) {
            state.changeState(NO_CARDS_LEFT_OF_THAT_TYPE_WARNING)
          } else {
            state.changeMoneyOfPlayer(state.CurrentTurnPlayerId, -card.price)
              .removeCardFromStack(card)
              .giveCard(state.CurrentTurnPlayerId, card)
              .changeState(EndofTurn)

          }

        case None => state.changeState(NONE_EXISTANT_CARDNAME_WARNING)
      }
    }

    def processRejection(gamestate1: Gamestate, reject: Boolean): Gamestate = {
      if (reject) {
        val (dicethrowA, rndManager1) = rndManager.getNextNum
        rndManager = rndManager1
        val (dicethrowB, rndManager2) = rndManager1.getNextNum
        rndManager = rndManager2
        var g2 = gamestate1.changeDiceResult(if (gamestate.diceChoosen == 2) dicethrowA + dicethrowB else dicethrowA)
          .changeState(Result2)
        notifiyObservers(g2)
        return g2.changeState(Cardeffects)

      } else {
        return gamestate1.changeState(Cardeffects) // goes directly to the effects
      }
    }
*/
  }
