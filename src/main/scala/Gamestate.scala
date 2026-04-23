package de.htwg.se.machikoro.remake

import scala.io.StdIn.readLine
import scala.util.Random


/*
enum Turnstate {
  case ChoosingDyeAmount, CheckingResult, BuyPhase, choosePlayerToPunish, choosePlayerToSwitchCardWith
}*/

case class Gamestate (val curentTurn : Int = 0,
                 val Players: List[Player],
                 val CurrentTurnPlayerId: Int = 0,
                 val DiceResult: Int = -1, 
                      val diceChoosen: Int = 1) {
  def changeMoneyOfPlayer(playerId: Int, amount: Int, cardtype: Type = Type.Secondary_Industry): Gamestate = {
    val updatedPlayers = Players.map { currentplayer =>
      if (currentplayer.playerId == playerId) {
        if(currentplayer.getExtraMoney() && cardtype == Type.Store){
          currentplayer.copy(money = currentplayer.money + amount + 1)
        }else{
          currentplayer.copy(money = currentplayer.money + amount)
        }
      } else currentplayer
    }
    this.copy(Players = updatedPlayers)
  }

  def transferMoneyBetweenPlayers(GiverPlayerId: Int,TakerPlayerId: Int, amount: Int, cardtype: Type = Type.Secondary_Industry): Gamestate = {
    if(TakerPlayerId == GiverPlayerId) return this.copy()//Cant steal money from yourself
   
    val updatedPlayers = Players.map { currentplayer =>
      if(Players.find(_.playerId == TakerPlayerId).exists(_.getExtraMoney()) && cardtype == Type.Restaurants){
        if (currentplayer.playerId == TakerPlayerId) currentplayer.copy(money = currentplayer.money + amount + 1)
        else if (currentplayer.playerId == GiverPlayerId) currentplayer.copy(money = currentplayer.money - amount -1)
        else currentplayer
      }else{
        if (currentplayer.playerId == TakerPlayerId) currentplayer.copy(money = currentplayer.money + amount)
        else if (currentplayer.playerId == GiverPlayerId) currentplayer.copy(money = currentplayer.money - amount)
        else currentplayer
      }
    }
    this.copy(Players = updatedPlayers)
  }
  def stealFromEveryone(ownerId: Int, amount: Int): Gamestate = {
    val updatedPlayers = Players.map { currentplayer =>
      if (currentplayer.playerId == ownerId) currentplayer.copy(money = currentplayer.money + amount * (Players.size-1))
      else currentplayer.copy(money = currentplayer.money - amount)
    }
    this.copy(Players = updatedPlayers)
  }
  def changeMoneyOfPlayerScaleByType(ownerId: Int, cardtype: Type,amount: Int): Gamestate = {
    val updatedPlayers = Players.map { currentplayer =>
      if (currentplayer.playerId == ownerId) {
        currentplayer.copy(money = currentplayer.money + amount * currentplayer.properties.count(_.cardType == cardtype))
      } // find cound with type
      else currentplayer
    }
    this.copy(Players = updatedPlayers)
  }
  
  def giveCard(ownerId: Int, newCard: card): Gamestate = {
    val updatedPlayers = Players.map { currentplayer =>
      if (currentplayer.playerId == ownerId) {
        currentplayer.copy(properties = newCard.copy(cardOwnerId = ownerId) :: currentplayer.properties)
      }
      else currentplayer
    }
    this.copy(Players = updatedPlayers)
  }
  def activateCards(rollNum: Int,rollerId: Int) : Gamestate = {
    var tmpGamestate = this
    for (p <- Players) {
      tmpGamestate = p.activateCards(rollNum, rollerId, tmpGamestate)
    }
    return tmpGamestate
  }
  def iterateTurn(): Gamestate = {
    //curentTurn += 1
    if(Players.find(_.playerId == CurrentTurnPlayerId).exists(_.GetsAnotherTurn)){
      val updatedPlayers = Players.map { currentplayer =>
        if (currentplayer.playerId == CurrentTurnPlayerId) {
          currentplayer.copy(GetsAnotherTurn = false)
        }
        else currentplayer
      }
      return this.copy(Players = updatedPlayers, curentTurn = (curentTurn + 1))
    } else {
      return this.copy( curentTurn = (curentTurn + 1), CurrentTurnPlayerId = ((CurrentTurnPlayerId + 1) % Players.size))
    }
  }
  /*
  def changeTurnstate(newTurnstate: Turnstate): Gamestate = {
    return this.copy(turnstate = newTurnstate)
  }
  */
  def choseDiceamount(): Gamestate = {
    val random = new Random()
    val dicethrowA = random.nextInt(6) + 1
    val dicethrowB = random.nextInt(6) + 1
    if(Players.find(_.playerId == curentTurn).exists(_.canChooseDyeAmount())){
      val diceAmount = getDiceAmount()
      if(diceAmount == 2){
        if(dicethrowA == dicethrowB && Players.find(_.playerId == curentTurn).exists(_.canGetAnotherTurn())){ //Pasch und die Karte die einem einem Weiter Zug gibt
          val updatedPlayers = Players.map { currentplayer =>
            if (currentplayer.playerId == CurrentTurnPlayerId) {
              currentplayer.copy(GetsAnotherTurn = true)
            }
            else currentplayer
          }
          return this.copy(diceChoosen = diceAmount,Players = updatedPlayers,DiceResult = dicethrowA + dicethrowB)
        }else{
          return this.copy(diceChoosen = diceAmount, DiceResult = dicethrowA + dicethrowB)
        }
      }else{
        return this.copy(diceChoosen = diceAmount, DiceResult = dicethrowA)
      }
    } else {
      return this.copy(diceChoosen = 1, DiceResult = dicethrowA)
    }
  }
  def getDiceAmount(): Int = {
    val input = readLine("How many Dice do you want to use?(1/2)")
    if(input.equals('1')){
      return 1
    }else if(input.equals('2')){
      return 2
    }else{
      print("BadInput type again")
      return getDiceAmount()
    }
  }/*
  def checkingResult(): Gamestate = {
    if (Players.find(_.playerId == curentTurn).exists(_.canRejectDyeTrow())) {
      
    }else {
      
    }
  }

  def choosePlayerToPunish(): Gamestate = {}

  def choosePlayerToSwitchCardWith(): Gamestate = {}
  
  def BuyPhase(): Gamestate = {}

*/

  //ChoosingDyeAmount, CheckingResult, BuyPhase, choosePlayerToPunish, choosePlayerToSwitchCardWith
}







