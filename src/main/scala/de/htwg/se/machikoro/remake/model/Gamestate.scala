package de.htwg.se.machikoro.remake.model


import de.htwg.se.machikoro.remake.model.allCardsBaseGame.*
import de.htwg.se.machikoro.remake.model.turnState.StartofTurn

import scala.io.StdIn.readLine
import scala.util.Random


val startMoneyPlayers = 100
case class cardStack(val amount : Int,
                     val stackCard : Card)

enum turnState {
  case StartofTurn, 
  ChooseDiceAmount,
  Result1,
  AskForRejectionOfResult,
  Result2,
  Cardeffects,
  Buyphase,
  EndofTurn,
  PlayerWins,
  ALREADY_OWN_THAT_YELLOW_CARD_WARNING,
  ALREADY_OWN_PURPLE_CARD_WARNING,
  NO_CARDS_LEFT_OF_THAT_TYPE_WARNING,
  YOU_CANT_AFFORD_THIS_WARNING,
  NONE_EXISTANT_CARDNAME_WARNING
  
}


case class Gamestate (val curentTurn : Int = 0,
                      val Players: List[Player] = List(),
                      val CurrentTurnPlayerId: Int = 0,
                      val DiceResult: Int = -1, 
                      val diceChoosen: Int = 1,
                      val cardStacks : List[cardStack] = List(),
                      val state : turnState = StartofTurn) 
{
 
  
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
  
  def giveCard(ownerId: Int, newCard: Card): Gamestate = {
    val updatedPlayers = Players.map { currentplayer =>
      if (currentplayer.playerId == ownerId) {
        currentplayer.copy(properties = newCard.copy(cardOwnerId = ownerId) :: currentplayer.properties)
      }
      else currentplayer
    }
    this.copy(Players = updatedPlayers)
  }

  def activateCards(rollNum: Int, rollerId: Int): Gamestate = {
    Players.foldLeft(this) { (state, p) =>
      p.activateCards(rollNum, rollerId, state)
    }
  }
  
  /*def activateCards(rollNum: Int,rollerId: Int) : Gamestate = {
    var tmpGamestate = this
    for (p <- Players) {
      tmpGamestate = p.activateCards(rollNum, rollerId, tmpGamestate)
    }
    return tmpGamestate
  }*/
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
  
  def currentPlayerHasWon(): Boolean = {
    return Players.find(_.playerId == CurrentTurnPlayerId).exists(_.hasWonTheGame())
  }

  def removeCardFromStack(cardToRemove: Card): Gamestate = {
    val updatedCardStacks = cardStacks.map { currentStack =>
      if (currentStack.stackCard.cardName.equals(cardToRemove.cardName)) {
        currentStack.copy(amount = currentStack.amount - 1)
      }
      else currentStack
    }
    return copy(cardStacks = updatedCardStacks)
  }
  def changeState(newState : turnState):Gamestate = {
    return this.copy(state = newState)
  }

  def changeDiceChosen(newAmount: Int): Gamestate = {
    return this.copy(diceChoosen = newAmount)
  }

  def changePlayers(newPlayers: List[Player]): Gamestate = {
    return this.copy(Players = newPlayers)
  }

  def changeDiceResult(newAmount: Int): Gamestate = {
    return this.copy(DiceResult = newAmount)
  }
}







