package de.htwg.se.machikoro.remake.model

import de.htwg.se.machikoro.remake.model.allCardsBaseGame.*
/*
Factory Pattern to switch out start position
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⡇⠀⢸⣿⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⡇⠀⢸⣿⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⡇⠀⢸⣿⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡀⠀⠀⠀⠀⠀⢸⣿⡇⠀⢸⣿⠀⠀
⠀⠀⢠⣷⡀⠀⠀⠀⠀⣼⣦⠀⠀⠀⠀⢠⣷⣄⠀⠀⠀⠀⢸⣿⡇⠀⢸⣿⠀⠀
⠀⠀⢸⣿⣿⣆⠀⠀⠀⣿⣿⣷⡀⠀⠀⢸⣿⣿⣧⠀⠀⠀⢸⣿⡇⠀⢸⣿⠀⠀
⠀⠀⣾⣿⣿⣿⣷⡀⢠⣿⣿⣿⣿⣆⠀⣼⣿⣿⣿⣷⣄⠀⢸⣿⡇⠀⢸⣿⠀⠀
⠀⠀⣿⣿⣿⣿⣿⣿⣾⣿⣿⣿⣿⣿⣷⣿⣿⣿⣿⣿⣿⣶⣼⣿⣧⣤⣾⣿⠀⠀
⠀⠀⣿⡟⠉⠉⢹⣿⡏⠉⠉⢻⣿⠉⠉⠉⣿⣿⠉⠉⢹⣿⣿⣿⣿⣿⣿⣿⠀⠀
⠀⠀⣿⣷⣶⣶⣾⣿⣷⣶⣶⣾⣿⣶⣶⣶⣿⣿⣶⣶⣾⣿⣿⣿⣿⣿⣿⣿⠀⠀
⠀⠀⣿⡏⠉⠉⢹⣿⡏⠉⠉⢹⣿⠉⠉⠉⣿⣿⠉⠉⢹⣿⣿⣿⣿⣿⣿⣿⠀⠀
⠀⠀⣿⣿⣶⣶⣾⣿⣷⣶⣶⣿⣿⣷⣶⣾⣿⣿⣶⣶⣾⣿⣿⣿⣿⣿⣿⣿⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
*/



object Game {
  def apply(n: Int, gametype: String): Gamestate = {
    gametype match {
      case "hell_of_weat" => initializeWeatHell().createGame(n)
      case _ | "standart" => initializeStandartGame().createGame(n)
    }
  }
}


class initializeStandartGame(){
  def createGame(playerAmount: Int): Gamestate = {
    val players = (0 until playerAmount).toList.map(i => Player(money = startMoneyPlayers, playerId = i, properties = List(starterweizenfeld.copy(cardOwnerId = i), starterbaeckerei.copy(cardOwnerId = i)))) //gives the players their start cards
    var gameState = new Gamestate(Players = players)
    return gameState.copy(cardStacks = List(
      new cardStack(6, weizenfeld.copy()),
      new cardStack(6, bauernhof.copy()),
      new cardStack(6, baeckerei.copy()),
      new cardStack(6, cafe.copy()),
      new cardStack(6, minimarkt.copy()),
      new cardStack(6, wald.copy()),
      new cardStack(0, buerohaus.copy()),
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
}

class initializeWeatHell(){
  def createGame(playerAmount: Int): Gamestate = {
    val players = (0 until playerAmount).toList.map(i => Player(money = startMoneyPlayers, playerId = i, properties = List(starterweizenfeld.copy(cardOwnerId = i), starterbaeckerei.copy(cardOwnerId = i)))) //gives the players their start cards
    var gameState = new Gamestate(Players = players)
    return gameState.copy(cardStacks = List(
      new cardStack(100, weizenfeld.copy()),
      new cardStack(4, bahnhof.copy()),
      new cardStack(4, einkaufszentrum.copy()),
      new cardStack(4, freizeitpark.copy()),
      new cardStack(4, funkturm.copy())
    ))
  }
}