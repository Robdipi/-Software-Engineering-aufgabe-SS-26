/*package de.htwg.se.machikoro.remake.controller

import de.htwg.se.machikoro.remake.model.*

trait ControllerState() {
  def execute(gamestate : Gamestate): Unit
  def changeState(turnstate : turnState,gamestate : Gamestate): {

  }
}
object initialiceStateMachiene e
}
object StartState extends ControllerState {
  override def execute(gamestate: Gamestate): Unit = {
    val gamestate2 = gamestate.changeState(turnState.StartofTurn)
    ControllerV3.notifiyObservers(gamestate) //starts input request
    if (gamestate.Players.find(_.playerId == gamestate.CurrentTurnPlayerId).exists(_.canChooseDyeAmount())) {
      gamestate = gamestate.changeState(turnState.ChooseDiceAmount)
      
    } else {
      gamestate = gamestate.changeState(turnState.Result1).changeDiceChosen(1)
      
    }
  }
}

object chooseDiceAmountState extends ControllerState {
  override def execute(gamestate: Gamestate): Unit =
}

*/