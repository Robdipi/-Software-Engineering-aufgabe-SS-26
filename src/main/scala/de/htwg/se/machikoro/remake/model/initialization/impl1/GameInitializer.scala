package de.htwg.se.machikoro.remake.model.initialization.impl1

import com.google.inject.Inject
import de.htwg.se.machikoro.remake.model.Data.{Gamestate, Player, cardStack, startMoneyPlayers}
import de.htwg.se.machikoro.remake.model.Data.AllCardsBaseGame.*
import de.htwg.se.machikoro.remake.model.initialization.GameInitializationSystem
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



class Game @Inject() extends GameInitializationSystem {
  def apply(n: Int, gametype: String): Gamestate = {
    gametype match {
      case "hell_of_weat" => InitializeWheatHell().createGame(n)
      case _ | "standard" => InitializeStandardGame().createGame(n)
    }
  }
}


class InitializeStandardGame {
  def createGame(playerAmount: Int): Gamestate = {
    val players = (0 until playerAmount).toList.map(i => Player(money = startMoneyPlayers, playerId = i, properties = List(starterweizenfeld.copy(cardOwnerId = i), starterbaeckerei.copy(cardOwnerId = i)))) //gives the players their start cards
    val gameState = Gamestate(Players = players)
    gameState.copy(cardStacks = List(
      cardStack(6, weizenfeld.copy()),
      cardStack(6, bauernhof.copy()),
      cardStack(6, baeckerei.copy()),
      cardStack(6, cafe.copy()),
      cardStack(6, minimarkt.copy()),
      cardStack(6, wald.copy()),
      cardStack(0, buerohaus.copy()),
      cardStack(4, stadion.copy()),
      cardStack(4, fernsehsender.copy()),
      cardStack(6, molkerei.copy()),
      cardStack(6, möbelfabrik.copy()),
      cardStack(6, familienRestaurant.copy()),
      cardStack(6, bergwerk.copy()),
      cardStack(6, apfelgarten.copy()),
      cardStack(6, markthalle.copy()),
      cardStack(4, bahnhof.copy()),
      cardStack(4, einkaufszentrum.copy()),
      cardStack(4, freizeitpark.copy()),
      cardStack(4, funkturm.copy())
    ))
  }
}

class InitializeWheatHell {
  def createGame(playerAmount: Int): Gamestate = {
    val players = (0 until playerAmount).toList.map(i => Player(money = startMoneyPlayers, playerId = i, properties = List(starterweizenfeld.copy(cardOwnerId = i), starterbaeckerei.copy(cardOwnerId = i)))) //gives the players their start cards
    val gameState = Gamestate(Players = players)
    gameState.copy(cardStacks = List(
      cardStack(100, weizenfeld.copy()),
      cardStack(4, bahnhof.copy()),
      cardStack(4, einkaufszentrum.copy()),
      cardStack(4, freizeitpark.copy()),
      cardStack(4, funkturm.copy())
    ))
  }
}