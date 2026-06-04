package de.htwg.se.machikoro.remake.controller

import de.htwg.se.machikoro.remake.model.Color.{Purple, Yellow}
import de.htwg.se.machikoro.remake.model.turnState.*
import de.htwg.se.machikoro.remake.model.*



sealed trait UserInput
case class ChooseDiceAmount(amount: Int) extends UserInput
case class BuyCard(cardName: String) extends UserInput
case class RejectDiceRoll(reject: Boolean) extends UserInput

object Controller extends viewObserverable{
  var gamestate = new Gamestate()
  var rndManager = new RandomnessManager()

  def handleInput(input: UserInput): Unit = input match {
    case ChooseDiceAmount(amount) =>
      gamestate = gamestate.copy(diceChoosen = amount, state = turnState.Result1)
      resultone()//goes to resultone in the map
    case BuyCard(cardName) =>
      gamestate = processBuyingCard(gamestate, cardName)
      notifiyObservers(gamestate)
      if(gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(_.hasWonTheGame())){
        gamestate = gamestate.copy(state = PlayerWins)
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

  def startphase():Unit = {
    gamestate = gamestate.copy(state = turnState.StartofTurn)
    notifiyObservers(gamestate) //starts input request
    if(gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(_.canChooseDyeAmount())){
      gamestate = gamestate.copy(state = turnState.ChooseDiceAmount)
      notifiyObservers(gamestate)//starts input request
    }else{
      gamestate =  gamestate.copy(diceChoosen = 1, state = turnState.Result1)
      resultone()//goes to resultone in the map iss only made in resultone
      //no notify as the result 
    }
  }

  def resultone(): Unit = {

    val (dicethrowA, rndManager1) = rndManager.getNextNum
    rndManager = rndManager1
    val (dicethrowB, rndManager2) = rndManager1.getNextNum
    rndManager = rndManager2
    if(dicethrowA == dicethrowB
      && gamestate.diceChoosen == 2
      && gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(_.canGetAnotherTurn())) { //Pasch und die Karte die einem einem Weiter Zug gibt only works on first throw
      val updatedPlayers = gamestate.Players.map { currentplayer =>
        if (currentplayer.playerId == gamestate.CurrentTurnPlayerId) {
          currentplayer.copy(GetsAnotherTurn = true)
        } else {
          currentplayer
        }
      }
      gamestate = gamestate.copy(Players = updatedPlayers)
    }

    gamestate = gamestate.copy(DiceResult = if(gamestate.diceChoosen==2) dicethrowA + dicethrowB else dicethrowA)
    notifiyObservers(gamestate)


    if (gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(_.canRejectDyeTrow())) {
      gamestate = gamestate.copy(state = turnState.AskForRejectionOfResult)
      notifiyObservers(gamestate) //starts input request
    } else {
      gamestate = gamestate.copy(state = Cardeffects) // goes directly to the buy
      //starts input request
      activateCards()
    }
  }
  def activateCards():Unit = {
    gamestate = gamestate.activateCards(gamestate.DiceResult, gamestate.CurrentTurnPlayerId)
    notifiyObservers(gamestate)
    gamestate = gamestate.copy(state = Buyphase)
    notifiyObservers(gamestate)
  }


  private def processBuyingCard(state: Gamestate, input: String): Gamestate = {
    if (input == "next") return state.copy(state = EndofTurn)

    state.cardStacks.find(_.stackCard.cardName == input) match {
      case Some(stack) =>
        val currentPlayer = state.Players.find(_.playerId == state.CurrentTurnPlayerId).get
        val card = stack.stackCard

        if (currentPlayer.money < card.price) {
          state.copy(state = YOU_CANT_AFFORD_THIS_WARNING)
        } else if (card.color == Yellow && currentPlayer.properties.exists(_.cardName == card.cardName)) {
          state.copy(state = ALREADY_OWN_THAT_YELLOW_CARD_WARNING)
        } else if (card.color == Purple && currentPlayer.properties.exists(_.color == Purple)) {
          state.copy(state = ALREADY_OWN_PURPLE_CARD_WARNING)
        } else if (stack.amount <= 0) {
          state.copy(state = NO_CARDS_LEFT_OF_THAT_TYPE_WARNING)
        } else {
          state.changeMoneyOfPlayer(state.CurrentTurnPlayerId, -card.price)
            .removeCardFromStack(card)
            .giveCard(state.CurrentTurnPlayerId, card)
            .copy(state = EndofTurn)

        }

      case None => state.copy(state = NONE_EXISTANT_CARDNAME_WARNING)
    }
  }
  def processRejection(gamestate1: Gamestate, reject: Boolean): Gamestate = {
    if(reject){
      val (dicethrowA, rndManager1) = rndManager.getNextNum
      rndManager = rndManager1
      val (dicethrowB, rndManager2) = rndManager1.getNextNum
      rndManager = rndManager2
      var g2 =  gamestate1.copy(DiceResult = if(gamestate.diceChoosen==2) dicethrowA + dicethrowB else dicethrowA, state = Result2)
      notifiyObservers(g2)
      return g2.copy(state = Cardeffects)

    }else{
      return gamestate1.copy(state = Cardeffects) // goes directly to the effects
    }
  }

}
