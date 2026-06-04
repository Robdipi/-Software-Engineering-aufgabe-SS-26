package de.htwg.se.machikoro.remake.controller.Depreciated

import de.htwg.se.machikoro.remake.controller.{RandomnessManager, viewObserverable}
import de.htwg.se.machikoro.remake.model.*
import de.htwg.se.machikoro.remake.model.turnState.{ChooseDiceAmount, Result1}
import de.htwg.se.machikoro.remake.view.Tui.TUI

import scala.io.StdIn.readLine
@deprecated
object controllerMain extends viewObserverable(){


  var randomnessManager = new RandomnessManager() // I have no Idea how to do It other than with var

  def gameloop(gamestate: Gamestate): Unit = {
    val gamestate1 = startphase(gamestate)
    notifiyObservers(gamestate1)

  }
  
  
  def startphase(gamestate : Gamestate):Gamestate = {
    //update visuals for current player
    if(gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(_.canChooseDyeAmount())){
      val gamestate2 = gamestate.copy(state = ChooseDiceAmount)
      notifiyObservers(gamestate2)
      return gamestate.copy(diceChoosen = 2)///value
    }else{
      return gamestate.copy(diceChoosen = 1, state = Result1)
    }
  }

/*
  def choseDiceamount(): Gamestate = {
   

    val gamestate0 = this

    val (dicethrowA, rndManager1) = rndManager.getNextNum
    val gamestate1 = this.copy(rndManager = rndManager1)

    val (dicethrowB, rndManager2) = rndManager1.getNextNum
    val gamestate2 = gamestate1.copy(rndManager = rndManager2)

    if (Players.find(_.playerId == CurrentTurnPlayerId).exists(_.canChooseDyeAmount())) {
      val (diceAmount, inputManagerState) = getDiceAmount(this.inputManager)
      if (diceAmount == 2) {
        if (dicethrowA == dicethrowB && Players.find(_.playerId == CurrentTurnPlayerId).exists(_.canGetAnotherTurn())) { //Pasch und die Karte die einem einem Weiter Zug gibt
          val updatedPlayers = Players.map { currentplayer =>
            if (currentplayer.playerId == CurrentTurnPlayerId) {
              currentplayer.copy(GetsAnotherTurn = true)
            }
            else currentplayer
          }
          print("you threw a " + (dicethrowA + dicethrowB))
          println("This was a Pasch so you get an extra Turn")
          return gamestate2.copy(diceChoosen = diceAmount, Players = updatedPlayers, DiceResult = dicethrowA + dicethrowB, inputManager = inputManagerState.inputManager)
        } else {
          print("you threw a " + (dicethrowA + dicethrowB))
          return gamestate2.copy(diceChoosen = diceAmount, DiceResult = dicethrowA + dicethrowB, inputManager = inputManagerState.inputManager)
        }
      } else {
        print("you threw a " + dicethrowA)
        return gamestate2.copy(diceChoosen = diceAmount, DiceResult = dicethrowA, inputManager = inputManagerState.inputManager)
      }
    } else {
      print("you threw a " + dicethrowA)
      return gamestate2.copy(diceChoosen = 1, DiceResult = dicethrowA)
    }
  }

  def getDiceAmount(inputManager1: InputManager): (Int, Gamestate) = {

    val (input, inputManager2) = inputManager1.getNextInput("How many Dice do you want to use?(1/2)")
    if (input.equals("1")) {
      return (1, this.copy(inputManager = inputManager2))
    } else if (input.equals("2")) {
      return (2, this.copy(inputManager = inputManager2))
    } else {
      println("BadInput! " + input)
      return getDiceAmount(inputManager2)
    }
  }

  def checkingResult(): Gamestate = {
    if (Players.find(_.playerId == CurrentTurnPlayerId).exists(_.canRejectDyeTrow())) {
      val (rejectionanswer, inputManager2) = askForRejection(inputManager)
      if (rejectionanswer) {
        val gamestate0 = this

        val (dicethrowA, rndManager1) = rndManager.getNextNum
        val gamestate1 = this.copy(rndManager = rndManager1)

        val (dicethrowB, rndManager2) = rndManager1.getNextNum
        val gamestate2 = gamestate1.copy(rndManager = rndManager2)
        if (this.diceChoosen == 2) {
          println("you threw a:" + (dicethrowA + dicethrowB))
          if (dicethrowA == dicethrowB && Players.find(_.playerId == CurrentTurnPlayerId).exists(_.canGetAnotherTurn())) { //Pasch und die Karte die einem einem Weiter Zug gibt
            val updatedPlayers = Players.map { currentplayer =>
              if (currentplayer.playerId == CurrentTurnPlayerId) {
                currentplayer.copy(GetsAnotherTurn = true)
              }
              else currentplayer
            }
            println("This was a Pasch so you get an extra Turn")
            return gamestate2.copy(DiceResult = dicethrowA + dicethrowB, Players = updatedPlayers, inputManager = inputManager2)
          } else {
            return gamestate2.copy(DiceResult = dicethrowA + dicethrowB, inputManager = inputManager2)
          }
        } else {
          println("you threw a:" + dicethrowA)
          return gamestate2.copy(DiceResult = dicethrowA, inputManager = inputManager2)
        }

      } else {
        return this.copy(inputManager = inputManager2)
      }
    } else {
      return this
    }
  }

  def askForRejection(startInputmanager: InputManager): (Boolean, InputManager) = {
    val (input, inputManager2) = startInputmanager.getNextInput(" are you happy with the number you got?(y/n) ")
    if (input.equals("y")) {
      return (false, inputManager2)
    } else if (input.equals("n")) {
      return (true, inputManager2)
    } else {
      println("BadInput! " + input)
      return askForRejection(inputManager2)
    }
  }

  def BuyPhase(): Gamestate = {
    println("")
    println("These Cards are currently available to buy: ")
    println("You have " + Players.find(_.playerId == CurrentTurnPlayerId).get.money + "€")

    println("")
    cardStacks.foreach(cardStack => println(s"${cardStack.stackCard.price}€-${cardStack.stackCard.cardName} x${cardStack.amount}"))
    println("")
    return askForCardToBuy(inputManager)
  }

  def removeCardFromStack(cardToRemove: card): Gamestate = {
    val updatedCardStacks = cardStacks.map { currentStack =>
      if (currentStack.stackCard.cardName.equals(cardToRemove.cardName)) {
        currentStack.copy(amount = currentStack.amount - 1)
      }
      else currentStack
    }
    return this.copy(cardStacks = updatedCardStacks)
  }

  def askForCardToBuy(inputManager: InputManager): Gamestate = {
    val (input, inputManager2) = inputManager.getNextInput("Type the name of a card to buy it or type 'next' to buy nothing")
    if (input.equals("next")) {
      return this.copy(inputManager = inputManager2)
    } else if (cardStacks.find(_.stackCard.cardName.equals(input)).isDefined) {

      val currentPlayer = Players.find(_.playerId == CurrentTurnPlayerId).get
      val currentCard = cardStacks.find(_.stackCard.cardName.equals(input)).get.stackCard
      val cardStack = cardStacks.find(_.stackCard.cardName.equals(input)).get


      if (currentPlayer.money >= currentCard.price) {
        if (currentCard.color == Yellow && currentPlayer.properties.find(_.cardName == currentCard.cardName).isDefined) {
          print("you already have this card!")
          return askForCardToBuy(inputManager2)
        } else if (currentCard.color == Purple && currentPlayer.properties.find(_.color == Purple).isDefined) {
          print("you already have a purple Card you can only have own!")
          return askForCardToBuy(inputManager2)
        } else {
          return actuallyBuyCard(currentCard, cardStack, inputManager2)
        }
      } else {
        print("you cant afford that!")
        return askForCardToBuy(inputManager2)
      }
    } else {
      println("BadInput! " + input)
      return askForCardToBuy(inputManager2)
    }
  }

  def actuallyBuyCard(currentCard: card, currentStack: cardStack, inputManager: InputManager): Gamestate = {
    if (currentStack.amount >= 1) {
      val tmpState = this.changeMoneyOfPlayer(CurrentTurnPlayerId, -1 * currentCard.price)
      val tmpState2 = tmpState.removeCardFromStack(currentCard)
      return tmpState2.giveCard(CurrentTurnPlayerId, currentCard).copy(inputManager = inputManager)
    } else {
      print("There are no more cards left of This!")
      return askForCardToBuy(inputManager)
    }

  }*/
}