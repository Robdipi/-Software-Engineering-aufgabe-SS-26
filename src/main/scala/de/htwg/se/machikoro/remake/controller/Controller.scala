package de.htwg.se.machikoro.remake.controller

import de.htwg.se.machikoro.remake.model.{turnState, *}
import de.htwg.se.machikoro.remake.model.turnState.*



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
      notifiyObservers(gamestate)
      resultone()//goes to resultone in the map
    case BuyCard(cardName) =>
      gamestate = processBuyingCard(gamestate, cardName)
      notifiyObservers(gamestate)
    case RejectDiceRoll(reject) =>
      gamestate = processRejection(gamestate, reject)
      notifiyObservers(gamestate)
      activateCards()
  }

  def startphase():Unit = {
    if(gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(_.canChooseDyeAmount())){
      gamestate = gamestate.copy(state = turnState.ChooseDiceAmount)
      notifiyObservers(gamestate)//starts input request
    }else{
      gamestate =  gamestate.copy(diceChoosen = 1, state = turnState.Result1)
      notifiyObservers(gamestate)//only show some stuff
      resultone()//goes to resultone in the map
    }
  }

  def resultone(): Unit = {

    val (dicethrowA, rndManager1) = rndManager.getNextNum
    rndManager = rndManager1
    val (dicethrowB, rndManager2) = rndManager1.getNextNum
    rndManager = rndManager2
    if(dicethrowA == dicethrowB
      && gamestate.diceChoosen == 2
      && gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(_.canGetAnotherTurn())) { //Pasch und die Karte die einem einem Weiter Zug gibt
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



    if (gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(_.canRejectDyeTrow())) {
      gamestate.copy(state = turnState.AskForRejectionOfResult)
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
















  def processBuyingCard(gamestate:Gamestate, cardName:String): Gamestate = {
    //buy logic end of turn logic TODO tommorrow
    return gamestate
  }

  def processRejection(gamestate1: Gamestate, reject: Boolean): Gamestate = {
    if(reject){
      val (dicethrowA, rndManager1) = rndManager.getNextNum
      rndManager = rndManager1
      val (dicethrowB, rndManager2) = rndManager1.getNextNum
      rndManager = rndManager2
      return gamestate1.copy(DiceResult = if(gamestate.diceChoosen==2) dicethrowA + dicethrowB else dicethrowA,
        state = Cardeffects)

    }else{
      return gamestate1.copy(state = Cardeffects) // goes directly to the effects

    }
  }

}
