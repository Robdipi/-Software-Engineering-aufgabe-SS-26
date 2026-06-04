package de.htwg.se.machikoro.remake.controller.Depreciated

/*package de.htwg.se.machikoro.remake.controller

import de.htwg.se.machikoro.remake.model.Color.{Purple, Yellow}
import de.htwg.se.machikoro.remake.model.turnState.*
import de.htwg.se.machikoro.remake.model.*


trait Command {
  def doStep: Gamestate
  def undoStep: Gamestate
}

class UndoManager {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command, currentState: Gamestate): Gamestate = {
    val newState = command.doStep
    undoStack = command :: undoStack
    redoStack = Nil
    newState
  }

  def undoStep(currentState: Gamestate): Gamestate = undoStack match {
    case Nil => currentState
    case head :: stack =>
      undoStack = stack
      redoStack = head :: redoStack
      head.undoStep
  }

  def redoStep(currentState: Gamestate): Gamestate = redoStack match {
    case Nil => currentState
    case head :: stack =>
      redoStack = stack
      undoStack = head :: undoStack
      head.doStep
  }
}

sealed trait UserInput
case class ChooseDiceAmount(amount: Int) extends UserInput
case class BuyCard(cardName: String) extends UserInput
case class RejectDiceRoll(reject: Boolean) extends UserInput

object ControllerV3 extends viewObserverable {

  var gamestate = Gamestate()
  private var rndManager = RandomnessManager()
  private val undoManager = new UndoManager()
  var winCondition: Player => Boolean = _.hasWonTheGameSmallRound()

  def handleInput(input: UserInput): Unit = input match {
    case ChooseDiceAmount(amount) =>
      doCommand(new ChooseDiceCommand(amount))

    case BuyCard(cardName) =>
      doCommand(new BuyCardCommand(cardName))

    case RejectDiceRoll(reject) =>
      doCommand(new RejectDiceCommand(reject))
  }

  private def doCommand(command: Command): Unit = {
    gamestate = undoManager.doStep(command, gamestate)
    notifiyObservers(gamestate)
  }


  class ChooseDiceCommand(amount: Int) extends Command {
    private var previousState: Gamestate = gamestate

    override def doStep: Gamestate = {
      previousState = gamestate
      setGamestate(gamestate.changeDiceChosen(amount).changeState(Result1))
      resultone(gamestate)
    }

    override def undoStep: Gamestate = previousState
  }
  class BuyCardCommand(cardName: String) extends Command {
    private var previousState: Gamestate = gamestate

    override def doStep: Gamestate = {
      previousState = gamestate
      val newState = processBuyingCard(gamestate, cardName)
      setGamestate(newState)
    }

    override def undoStep: Gamestate = previousState
  }

  class RejectDiceCommand(reject: Boolean) extends Command {
    private var previousState: Gamestate = gamestate

    override def doStep: Gamestate = {
      previousState = gamestate
      val newState = processRejection(gamestate, reject)
      setGamestate(newState)
    }

    override def undoStep: Gamestate = previousState
  }




  private def resultone(state: Gamestate): Gamestate = {
    val (dicethrowA, rndManager1) = rndManager.getNextNum
    rndManager = rndManager1
    val (dicethrowB, rndManager2) = rndManager1.getNextNum
    rndManager = rndManager2

    val updatedPlayers = state.Players.map { currentplayer =>
      if (currentplayer.playerId == state.CurrentTurnPlayerId &&
        dicethrowA == dicethrowB &&
        state.diceChoosen == 2 &&
        currentplayer.canGetAnotherTurn())
        currentplayer.copy(GetsAnotherTurn = true)
      else currentplayer
    }

    var newState = state
      .changePlayers(updatedPlayers)
      .changeDiceResult(if (state.diceChoosen == 2) dicethrowA + dicethrowB else dicethrowA)

    setGamestate(newState)

    if (state.Players.exists(p => p.playerId == state.CurrentTurnPlayerId && p.canRejectDyeTrow())) {
      setGamestate(newState.changeState(turnState.AskForRejectionOfResult))
    } else {
      setGamestate(activateCards(newState))
    }
  }

  def activateCards(state: Gamestate): Gamestate = {
    state.activateCards(state.DiceResult, state.CurrentTurnPlayerId).changeState(Buyphase)
  }

  private def processBuyingCard(state: Gamestate, input: String): Gamestate = {
    if (input == "next") return setGamestate(state.changeState(EndofTurn))

    state.cardStacks.find(_.stackCard.cardName == input) match {
      case Some(stack) =>
        val currentPlayer = state.Players.find(_.playerId == state.CurrentTurnPlayerId).get
        val card = stack.stackCard

        if (currentPlayer.money < card.price) {
          state.changeState(YOU_CANT_AFFORD_THIS_WARNING)
        } else if (card.color == Yellow && currentPlayer.properties.exists(_.cardName == card.cardName)) {
          state.changeState(ALREADY_OWN_THAT_YELLOW_CARD_WARNING)
        } else if (card.color == Purple && currentPlayer.properties.exists(_.color == Purple)) {
          state.changeState(ALREADY_OWN_PURPLE_CARD_WARNING)
        } else if (stack.amount <= 0) {
          state.changeState(NO_CARDS_LEFT_OF_THAT_TYPE_WARNING)
        } else {
          setGamestate(state.changeMoneyOfPlayer(state.CurrentTurnPlayerId, -card.price)
            .removeCardFromStack(card)
            .giveCard(state.CurrentTurnPlayerId, card)
            .changeState(EndofTurn))
        }

      case None => state.changeState(NONE_EXISTANT_CARDNAME_WARNING)
    }
  }

  private def processRejection(state: Gamestate, reject: Boolean): Gamestate = {
    if (reject) {
      val (dicethrowA, rndManager1) = rndManager.getNextNum
      rndManager = rndManager1
      val (dicethrowB, rndManager2) = rndManager1.getNextNum
      rndManager = rndManager2
      state.changeDiceResult(if (state.diceChoosen == 2) dicethrowA + dicethrowB else dicethrowA)
        .changeState(Result2)
    } else {
      state.changeState(Cardeffects)
    }
  }

  private def setGamestate(newState: Gamestate): Gamestate = {
    gamestate = newState
    notifiyObservers(gamestate)
    gamestate
  }

  def startTurn(): Unit = {
    gamestate = gamestate.changeState(turnState.StartofTurn)
    if (gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(_.canChooseDyeAmount())) {
      gamestate = gamestate.changeState(turnState.ChooseDiceAmount)
      notifiyObservers(gamestate)

    } else {
      gamestate = gamestate.changeState(turnState.Result1).changeDiceChosen(1)
      gamestate = resultone(gamestate)
      //goes to resultone in the map iss only made in resultone
      //no notify as the result
    }

  }
  def endOfTurn(): Unit = {
    if (gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(winCondition)) {
      setGamestate(gamestate.iterateTurn().changeState(PlayerWins))
    } else {
      setGamestate(gamestate.iterateTurn().changeState(StartofTurn))
    }
  }
  def tryToBuy(): Unit = {
    gamestate = gamestate.changeState(Buyphase)
    notifiyObservers(gamestate)
  }

}*/