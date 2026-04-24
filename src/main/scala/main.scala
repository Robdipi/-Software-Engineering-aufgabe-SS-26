package de.htwg.se.machikoro.remake;
import de.htwg.se.machikoro.remake.*

import scala.io.StdIn.readLine
object main {
  def main(args: Array[String]): Unit = {
    gameloop(1)
  }
  def gameloop(playerAmount:Int): Unit = {
    var gameState = new Gamestate()
    gameState = gameState.initializeStandartGame(playerAmount) //all start cards in the middle

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
