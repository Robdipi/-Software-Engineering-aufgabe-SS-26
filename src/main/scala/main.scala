package de.htwg.se.machikoro.remake;
import de.htwg.se.machikoro.remake.allCardsBaseGame.*

import scala.io.StdIn.readLine
object main {
  def main(args: Array[String]): Unit = {
    gameloop(1)
  }
  def gameloop(playerAmount:Int): Unit = {
    val players = (0 until playerAmount).toList.map(i => Player(money = 100,playerId =  i,
      properties = List(starterweizenfeld.copy(cardOwnerId = i),starterbaeckerei.copy(cardOwnerId = i))))//gives the players their start cards
    var gameState = new Gamestate(Players = players)
    gameState = gameState.initializeStandartGame() //all start cards in the middle

    var gameIsRunning = true
    while (gameIsRunning){
      gameState = gameState.choseDiceamount()
      gameState = gameState.checkingResult()
      gameState = gameState.activateCards(gameState.DiceResult,gameState.CurrentTurnPlayerId)
      gameState = gameState.BuyPhase()
      if(gameState.currentPlayerHasWon()){
        gameIsRunning = false
      }else{
        gameState = gameState.iterateTurn()
      }
    }
    println("---------------------------------------------------------")
    println("Player " + (gameState.CurrentTurnPlayerId + 1) + " has Won!")
    println("---------------------------------------------------------")

  }
}
