package de.htwg.se.machikoro.remake;
import de.htwg.se.machikoro.remake.*

import scala.io.StdIn.readLine
object main {
  def main(args: Array[String]): Unit = {
    val gameState = new Gamestate().initializeStandartGame(2)
    gameloop(gameState)
  }
  def gameloop(gameState : Gamestate): Unit = {
    val gameState1 = gameState.choseDiceamount().checkingResult()
    val gameState2 = gameState1.activateCards(gameState1.DiceResult,gameState1.CurrentTurnPlayerId)
      .BuyPhase()
      if(gameState2.currentPlayerHasWon()){
        println("---------------------------------------------------------")
        println("Player " + (gameState2.CurrentTurnPlayerId + 1) + " has Won!")
        println("---------------------------------------------------------")
      }else{
        gameloop(gameState2.iterateTurn())
      }
    }
}