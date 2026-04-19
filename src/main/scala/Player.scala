package de.htwg.se.machikoro.remake

import allCardsBaseGame.*

case class Player (val money: Int = 0,
                   val properties: List[card] = List(), // var because I dont want to have to changeout the player because I want to be able to refferenz a specific player to test
                   val GetsAnotherTurn: Boolean = false,
                   val playerId: Int) {
  def canChooseDyeAmount(): Boolean = {return properties.find(_.cardName == bahnhof.cardName).isDefined} //conteins didnt work
  def canGetAnotherTurn(): Boolean = {return properties.find(_.cardName == freizeitpark.cardName).isDefined}
  def canRejectDyeTrow(): Boolean = {return properties.find(_.cardName == funkturm.cardName).isDefined}
  def getExtraMoney(): Boolean = {return properties.find(_.cardName == einkaufszentrum.cardName).isDefined}
}