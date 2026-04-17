package de.htwg.se.machikoro.remake

import allCardsBaseGame.*

case class Player (val playerId : Int = -1,
              val money: Int = 0,
              val properties: List[card] = List(),
              val GetsAnotherTurn: Boolean = false) {
  def canChooseDyeAmount(): Boolean = {return properties.contains(weizenfeld.copy(cardOwnerID = playerId))}
  def canGetAnotherTurn(): Boolean = {return properties.contains(weizenfeld.copy(cardOwnerID = playerId))}
  def canRejectDyeTrow(): Boolean = {return properties.contains(weizenfeld.copy(cardOwnerID = playerId))}
  def getExtraMoney(): Boolean = {return properties.contains(weizenfeld.copy(cardOwnerID = playerId))}
}