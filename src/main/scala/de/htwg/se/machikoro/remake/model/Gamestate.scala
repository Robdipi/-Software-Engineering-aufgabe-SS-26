package de.htwg.se.machikoro.remake.model


import scala.io.StdIn.readLine
import scala.util.Random


val startMoneyPlayers = 100
case class cardStack(val amount : Int,
                     val stackCard : card)
case class Gamestate (val curentTurn : Int = 0,
                 val Players: List[Player] = List(),
                 val CurrentTurnPlayerId: Int = 0,
                 val DiceResult: Int = -1, 
                      val diceChoosen: Int = 1,
                      val cardStacks : List[cardStack] = List()) 
{
  
  def initializeStandartGame(playerAmount:Int): Gamestate = {
    val players = (0 until playerAmount).toList.map(i => Player(money = startMoneyPlayers, playerId = i, properties = List(starterweizenfeld.copy(cardOwnerId = i), starterbaeckerei.copy(cardOwnerId = i)))) //gives the players their start cards
    var gameState = this.copy(Players = players)
    return gameState.copy(cardStacks = List(
      new cardStack(6, weizenfeld.copy()),
      new cardStack(6, bauernhof.copy()),
      new cardStack(6, baeckerei.copy()),
      new cardStack(6, cafe.copy()),
      new cardStack(6, minimarkt.copy()),
      new cardStack(6, wald.copy()),
      new cardStack(4, buerohaus.copy()),
      new cardStack(4, stadion.copy()),
      new cardStack(4, fernsehsender.copy()),
      new cardStack(6, molkerei.copy()),
      new cardStack(6, möbelfabrik.copy()),
      new cardStack(6, familienRestaurant.copy()),
      new cardStack(6, bergwerk.copy()),
      new cardStack(6, apfelgarten.copy()),
      new cardStack(6, markthalle.copy()),
      new cardStack(4, bahnhof.copy()),
      new cardStack(4, einkaufszentrum.copy()),
      new cardStack(4, freizeitpark.copy()),
      new cardStack(4, funkturm.copy())
    ))
  }
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
}







