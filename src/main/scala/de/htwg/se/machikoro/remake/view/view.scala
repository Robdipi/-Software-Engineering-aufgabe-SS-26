package de.htwg.se.machikoro.remake.view



abstract class view extends viewObserver {
  def getDiceAmount():Int//ask how many dyes to choose
  def showDiceResult(diceA : Int, diceB : Int, diceChoosen : Int) : Unit
  def askToRejectResult():Boolean//ask if the player is happy with his Result if can
  def askWhatToBuy():String // GIVES
  def pashEffect() : Unit // maybe konfetti on the GUI because you get an extra turn
  
  def badInputWarning() : Unit

  def cantBuyWarningToExpensive(): Unit
  def cantBuyWarningNothingLeft(): Unit


  def showAvailableCards() : Unit
  
  
  def showWinner(playerId : Int): Unit//show which player won the game
}
