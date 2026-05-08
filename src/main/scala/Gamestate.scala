package de.htwg.se.machikoro.remake

import de.htwg.se.machikoro.remake.Color.*
import de.htwg.se.machikoro.remake.allCardsBaseGame.*

import javax.smartcardio.Card
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
                      val cardStacks : List[cardStack] = List(),
                      val rndManager : RandomnessManager = new RandomnessManager(),
                      val inputManager : InputManager = new InputManager()) 
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
  /*
  def changeTurnstate(newTurnstate: Turnstate): Gamestate = {
    return this.copy(turnstate = newTurnstate)
  }
  */
  def choseDiceamount(): Gamestate = {
    println("---------------------------------------------------------")
    println("Player " + (this.CurrentTurnPlayerId + 1) + "'s Turn")
    Players.foreach(_.printAllCards())
    println("")

    val gamestate0 = this

    val (dicethrowA, rndManager1) = rndManager.getNextNum
    val gamestate1 = this.copy(rndManager = rndManager1)

    val (dicethrowB, rndManager2) = rndManager1.getNextNum
    val gamestate2 = gamestate1.copy(rndManager = rndManager2)

    if(Players.find(_.playerId == CurrentTurnPlayerId).exists(_.canChooseDyeAmount())){
      val (diceAmount, inputManagerState) = getDiceAmount(this.inputManager)
      if(diceAmount == 2){
        if(dicethrowA == dicethrowB && Players.find(_.playerId == CurrentTurnPlayerId).exists(_.canGetAnotherTurn())){ //Pasch und die Karte die einem einem Weiter Zug gibt
          val updatedPlayers = Players.map { currentplayer =>
            if (currentplayer.playerId == CurrentTurnPlayerId) {
              currentplayer.copy(GetsAnotherTurn = true)
            }
            else currentplayer
          }
          print("you threw a " + (dicethrowA + dicethrowB))
          println("This was a Pasch so you get an extra Turn")
          return gamestate2.copy(diceChoosen = diceAmount,Players = updatedPlayers,DiceResult = dicethrowA + dicethrowB, inputManager = inputManagerState.inputManager)
        }else{
          print("you threw a " + (dicethrowA + dicethrowB))
          return gamestate2.copy(diceChoosen = diceAmount, DiceResult = dicethrowA + dicethrowB, inputManager = inputManagerState.inputManager)
        }
      }else{
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
    if(input.equals("1")){
      return (1, this.copy(inputManager = inputManager2))
    }else if(input.equals("2")){
      return (2, this.copy(inputManager = inputManager2))
    }else{
      println("BadInput! " + input)
      return getDiceAmount(inputManager2)
    }
  }

  def checkingResult(): Gamestate = {
    if (Players.find(_.playerId == CurrentTurnPlayerId).exists(_.canRejectDyeTrow())){
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
            return gamestate2.copy(DiceResult = dicethrowA + dicethrowB, Players = updatedPlayers,inputManager = inputManager2)
          } else {
            return gamestate2.copy(DiceResult = dicethrowA + dicethrowB,inputManager = inputManager2)
          }
        } else {
          println("you threw a:" + dicethrowA)
          return gamestate2.copy(DiceResult = dicethrowA,inputManager = inputManager2)
        }

      } else {
        return this.copy(inputManager = inputManager2)
      }
    }else {
      return this
    }
  }

  def askForRejection(startInputmanager : InputManager): (Boolean , InputManager)= {
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
    println("You have "+ Players.find(_.playerId == CurrentTurnPlayerId).get.money +"€")

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
    val (input,inputManager2) = inputManager.getNextInput("Type the name of a card to buy it or type 'next' to buy nothing")
    if (input.equals("next")) {
      return this.copy(inputManager = inputManager2)
    } else if (cardStacks.find(_.stackCard.cardName.equals(input)).isDefined) {

      val currentPlayer = Players.find(_.playerId == CurrentTurnPlayerId).get
      val currentCard = cardStacks.find(_.stackCard.cardName.equals(input)).get.stackCard
      val cardStack = cardStacks.find(_.stackCard.cardName.equals(input)).get


      if (currentPlayer.money >= currentCard.price) {
        if(currentCard.color == Yellow && currentPlayer.properties.find(_.cardName == currentCard.cardName).isDefined){
            print("you already have this card!")
            return askForCardToBuy(inputManager2)
        }else if(currentCard.color == Purple&&currentPlayer.properties.find(_.color == Purple).isDefined) {
          print("you already have a purple Card you can only have own!")
          return askForCardToBuy(inputManager2)
        } else{
        return actuallyBuyCard(currentCard,cardStack,inputManager2)
        }
      }else{
        print("you cant afford that!")
        return askForCardToBuy(inputManager2)
      }
    } else {
      println("BadInput! " + input)
      return askForCardToBuy(inputManager2)
    }
  }
  def actuallyBuyCard(currentCard:card,currentStack:cardStack,inputManager: InputManager):Gamestate = {
    if(currentStack.amount >= 1){
      val tmpState = this.changeMoneyOfPlayer(CurrentTurnPlayerId, -1 * currentCard.price)
      val tmpState2 = tmpState.removeCardFromStack(currentCard)
      return tmpState2.giveCard(CurrentTurnPlayerId, currentCard).copy(inputManager = inputManager)
    }else{
      print("There are no more cards left of This!")
      return askForCardToBuy(inputManager)
    }

  }
  def currentPlayerHasWon(): Boolean = {
    return Players.find(_.playerId == CurrentTurnPlayerId).exists(_.hasWonTheGame())
  }
}







