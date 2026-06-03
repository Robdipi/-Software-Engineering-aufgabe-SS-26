package de.htwg.se.machikoro.remake.view.Tui

import de.htwg.se.machikoro.remake.controller.viewObserver
import de.htwg.se.machikoro.remake.model.*


class TUI (extends viewObserver{
  
  def update(gamestate: Gamestate): Unit = {
    background(gamestate)
    overlay(gamestate)
  }
  def background(gamestate: Gamestate): Unit = {
    
  }
  def overlay(gamestate: Gamestate): Unit = {
    gamestate.state match {
      case turnState.StartofTurn => {}
      case turnState.ChooseDiceAmount => {
        val (diceamount, inputManager2) controllerMain getDiceAmount()}
      case turnState.Result1 => {}
      case turnState.AskForRejectionOfResult => {}
      case turnState.Result2 => {}
      case turnState.Cardeffects => {}
      case turnState.Buyphase => {}
      case turnState.PlayerWins => {}
    }
  }


  def getDiceAmount(inputManager1: InputManager): (Int, InputManager) = {

    val (input, inputManager2) = inputManager1.getNextInput("How many Dice do you want to use?(1/2)")
    if (input.equals("1")) {
      return (1, inputManager2)
    } else if (input.equals("2")) {
      return (2, inputManager2)
    } else {
      println("BadInput! " + input)
      return getDiceAmount(inputManager2)
    }
  }
}
