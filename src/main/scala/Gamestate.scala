package de.htwg.se.machikoro.remake

enum Turnstate {
  case ChoosingDyeAmount, CheckingResult, BuyPhase, choosePlayerToPunish, choosePlayerToSwitchCardWith
}

case class Gamestate (val curentTurn : Int = 0,
                 val Players: List[Player],
                 val CurrentTurnPlayerID: Int = 0,
                 val DyeResult: Int = -1) {
  def changeMoneyOfPlayer(playerId: Int, amount: Int): Gamestate = {
    val updatedPlayers = Players.map { player =>
      if (player.playerId == playerId) player.copy(money = player.money + amount)
      else player
    }
    this.copy(Players = updatedPlayers)
  }

  def transferMoneyBetweenPlayers(GiverPlayerId: Int,TakerPlayerId: Int, amount: Int): Gamestate = {
    if(TakerPlayerId == GiverPlayerId) return this.copy()//Cant steal money from yourself
    val updatedPlayers = Players.map { player =>
      if (player.playerId == TakerPlayerId) player.copy(money = player.money + amount)
      else if (player.playerId == GiverPlayerId) player.copy(money = player.money - amount)
      else player
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
}







