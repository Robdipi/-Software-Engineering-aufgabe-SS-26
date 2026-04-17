package de.htwg.se.machikoro.remake

import de.htwg.se.machikoro.remake.Turnstate.*


enum Turnstate {
  case ChoosingDyeAmount, CheckingResult, BuyPhase, choosePlayerToPunish, choosePlayerToSwitchCardWith
}

case class Gamestate (val curentTurn : Int = 0,
                 val Players: List[Player],
                 val CurrentTurnPlayerID: Int = 0,
                 val DyeResult: Int = -1,
                      turnstate: Turnstate = ChoosingDyeAmount) {
  def changeMoneyOfPlayer(playerId: Int, amount: Int, cardtype: Type = Type.Secondary_Industry): Gamestate = {
    val updatedPlayers = Players.map { player =>
      if (player.playerId == playerId) {
        if(/*player.getExtraMoney() && */cardtype == Type.Store){
          player.copy(money = player.money + amount + 1)
        }else{
          player.copy(money = player.money + amount)
        }
      } else player
    }
    this.copy(Players = updatedPlayers)
  }

  def transferMoneyBetweenPlayers(GiverPlayerId: Int,TakerPlayerId: Int, amount: Int, cardtype: Type = Type.Secondary_Industry): Gamestate = {
    if(TakerPlayerId == GiverPlayerId) return this.copy()//Cant steal money from yourself
   /* val getExtraMoney = Players.find(_.playerId == 1).exists(_.getExtraMoney())*/
    val updatedPlayers = Players.map { player =>
      if(/*getExtraMoney &&*/ cardtype == Type.Restaurants){
        if (player.playerId == TakerPlayerId) player.copy(money = player.money + amount + 1)
        else if (player.playerId == GiverPlayerId) player.copy(money = player.money - amount -1)
        else player
      }else{
        if (player.playerId == TakerPlayerId) player.copy(money = player.money + amount)
        else if (player.playerId == GiverPlayerId) player.copy(money = player.money - amount)
        else player
      }
    }
    this.copy(Players = updatedPlayers)
  }
  def stealFromEveryone(ownerId: Int, amount: Int): Gamestate = {
    val updatedPlayers = Players.map { player =>
      if (player.playerId == ownerId) player.copy(money = player.money + amount * (Players.size-1))
      else  player.copy(money = player.money - amount)
    }
    this.copy(Players = updatedPlayers)
  }
  def changeMoneyOfPlayerScaleByType(ownerId: Int, cardtype: Type,amount: Int): Gamestate = {
    val updatedPlayers = Players.map { player =>
      if (player.playerId == ownerId) {
        player.copy(money = player.money + amount * player.properties.count(_.cardType == cardtype))
      } // find cound with type
      else player
    }
    this.copy(Players = updatedPlayers)
  }
  /*
  def giveCard(ownerId: Int, newCard: card): Gamestate = {
    val updatedPlayers = Players.map { player =>
      if (player.playerId == ownerId) {
        player.copy(properties = newCard.copy(cardOwnerID = ownerId) :: player.properties)
      }
      else player
    }
    this.copy(Players = updatedPlayers)
  }
  */
   
}







