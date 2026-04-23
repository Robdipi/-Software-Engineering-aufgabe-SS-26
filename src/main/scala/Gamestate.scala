package de.htwg.se.machikoro.remake

import de.htwg.se.machikoro.remake.Color.*
import de.htwg.se.machikoro.remake.allCardsBaseGame.*

import javax.smartcardio.Card
import scala.io.StdIn.readLine
import scala.util.Random


/*
enum Turnstate {
  case ChoosingDyeAmount, CheckingResult, BuyPhase, choosePlayerToPunish, choosePlayerToSwitchCardWith
}*/
case class cardStack(val amount : Int,
                     val stackCard : card)
case class Gamestate (val curentTurn : Int = 0,
                 val Players: List[Player],
                 val CurrentTurnPlayerId: Int = 0,
                 val DiceResult: Int = -1,
                      val diceChoosen: Int = 1,
                      val cardStacks : List[cardStack] = List()) {
  def initializeStandartGame(): Gamestate = {
    return this.copy(cardStacks = List(
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
  def activateCards(rollNum: Int,rollerId: Int) : Gamestate = {
    var tmpGamestate = this
    for (p <- Players) {
      tmpGamestate = p.activateCards(rollNum, rollerId, tmpGamestate)
    }
    return tmpGamestate
  }
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
    println("")
    val random = new Random()
    val dicethrowA = random.nextInt(6) + 1
    val dicethrowB = random.nextInt(6) + 1
    if(Players.find(_.playerId == CurrentTurnPlayerId).exists(_.canChooseDyeAmount())){
      val diceAmount = getDiceAmount()
      if(diceAmount == 2){
        if(dicethrowA == dicethrowB && Players.find(_.playerId == CurrentTurnPlayerId).exists(_.canGetAnotherTurn())){ //Pasch und die Karte die einem einem Weiter Zug gibt
          val updatedPlayers = Players.map { currentplayer =>
            if (currentplayer.playerId == CurrentTurnPlayerId) {
              currentplayer.copy(GetsAnotherTurn = true)
            }
            else currentplayer
          }
          print("you threw a " + (dicethrowA + dicethrowB))
          return this.copy(diceChoosen = diceAmount,Players = updatedPlayers,DiceResult = dicethrowA + dicethrowB)
        }else{
          print("you threw a " + (dicethrowA + dicethrowB))
          return this.copy(diceChoosen = diceAmount, DiceResult = dicethrowA + dicethrowB)
        }
      }else{
        print("you threw a " + dicethrowA)
        return this.copy(diceChoosen = diceAmount, DiceResult = dicethrowA)
      }
    } else {
      print("you threw a " + dicethrowA)
      return this.copy(diceChoosen = 1, DiceResult = dicethrowA)
    }
  }
  def getDiceAmount(): Int = {

    val input = readLine("How many Dice do you want to use?(1/2)")
    if(input.equals("1")){
      return 1
    }else if(input.equals("2")){
      return 2
    }else{
      print("BadInput!")
      return getDiceAmount()
    }
  }

  def checkingResult(): Gamestate = {
    if (Players.find(_.playerId == curentTurn).exists(_.canRejectDyeTrow())&&askForRejection()) {
      val random = new Random()
      val dicethrowA = random.nextInt(6) + 1
      val dicethrowB = random.nextInt(6) + 1
      if(this.diceChoosen == 2) {
        print("you threw a:" + (dicethrowA + dicethrowB))
        if (dicethrowA == dicethrowB && Players.find(_.playerId == curentTurn).exists(_.canGetAnotherTurn())) { //Pasch und die Karte die einem einem Weiter Zug gibt
          val updatedPlayers = Players.map { currentplayer =>
            if (currentplayer.playerId == CurrentTurnPlayerId) {
              currentplayer.copy(GetsAnotherTurn = true)
            }
            else currentplayer
          }
          return this.copy(DiceResult = dicethrowA + dicethrowB, Players = updatedPlayers)
        }else{
          return this.copy(DiceResult = dicethrowA + dicethrowB)
        }
      }else{
        print("you threw a:" + dicethrowA)
        return this.copy(DiceResult = dicethrowA)
      }
    }else {
      return this
    }
  }

  def askForRejection(): Boolean = {
    val input = readLine("Are you happy with the number you got?(y/n)")
    if (input.equals('y')) {
      return false
    } else if (input.equals('n')) {
      return true
    } else {
      print("BadInput!")
      return askForRejection()
    }
  }
  def BuyPhase(): Gamestate = {
    println("")
    println("These Cards are currently available to buy: ")
    println("You have "+ Players.find(_.playerId == CurrentTurnPlayerId).get.money +"€")

    println("")
    cardStacks.foreach(cardStack => println(s"${cardStack.stackCard.price}€-${cardStack.stackCard.cardName} x${cardStack.amount}"))
    println("")
    return askForCardToBuy()
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

  def askForCardToBuy(): Gamestate = {
    val input = readLine("Type the name of a card to buy it or type 'next' to buy nothing")
    if (input.equals("next")) {
      return this
    } else if (cardStacks.find(_.stackCard.cardName.equals(input)).isDefined) {
      val playerOption = Players.find(_.playerId == CurrentTurnPlayerId)
      val cardStackOption = cardStacks.find(_.stackCard.cardName.equals(input))
      if (playerOption.exists(player =>
        cardStackOption.exists(cardStack =>
          player.money >= cardStack.stackCard.price))) {

        val currentPlayer = this.Players.find(_.playerId == CurrentTurnPlayerId).get
        val currentCard = cardStackOption.get.stackCard
        if(currentCard.color == Yellow){
          if(currentPlayer.properties.find(_.cardName == currentCard.cardName).isDefined){
            print("you already have this card!")
            return askForCardToBuy()
          }else{
            return actuallyBuyCard(currentCard,cardStackOption.get)
          }
        }else if(currentCard.color == Purple){
          if (currentPlayer.properties.find(_.color == Purple).isDefined) {
            print("you already have a purple Card you can only have own!")
            return askForCardToBuy()
          }else{
            return actuallyBuyCard(currentCard,cardStackOption.get)
          }
        } else{
          return actuallyBuyCard(currentCard,cardStackOption.get)
        }
      }else{
        print("you cant afford that!")
        return askForCardToBuy()
      }
    } else {
      print("BadInput!")
      return askForCardToBuy()
    }
  }
  def actuallyBuyCard(currentCard:card,currentStackOpt:cardStack):Gamestate = {
    if(currentStackOpt.amount >= 1){
      val tmpState = this.changeMoneyOfPlayer(CurrentTurnPlayerId, -1 * currentCard.price)
      val tmpState2 = tmpState.removeCardFromStack(currentCard)
      return tmpState2.giveCard(CurrentTurnPlayerId, currentCard)
    }else{
      print("There are no more cards left of This!")
      return askForCardToBuy()
    }

  }
  def currentPlayerHasWon(): Boolean = {
    return Players.find(_.playerId == CurrentTurnPlayerId).exists(_.hasWonTheGame())
  }

  /*def choosePlayerToPunish(): Gamestate = {}

  def choosePlayerToSwitchCardWith(): Gamestate = {}*/
}







