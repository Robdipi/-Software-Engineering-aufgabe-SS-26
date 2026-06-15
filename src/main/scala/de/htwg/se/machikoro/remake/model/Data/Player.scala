package de.htwg.se.machikoro.remake.model.Data

import Color.*
import allCardsBaseGame.*


case class Player (val money: Int = 0,
                   val properties: List[Card] = List(), // var because I dont want to have to changeout the player because I want to be able to refferenz a specific player to test
                   val GetsAnotherTurn: Boolean = false,
                   val playerId: Int) {
  def canChooseDyeAmount(): Boolean = {return properties.find(_.cardName == bahnhof.cardName).isDefined} //contains didnt work
  def canGetAnotherTurn(): Boolean = {return properties.find(_.cardName == freizeitpark.cardName).isDefined}
  def canRejectDyeTrow(): Boolean = {return properties.find(_.cardName == funkturm.cardName).isDefined}
  def getExtraMoney(): Boolean = {return properties.find(_.cardName == einkaufszentrum.cardName).isDefined}

  def hasWonTheGame(): Boolean = {
    return canChooseDyeAmount() && canGetAnotherTurn() && canRejectDyeTrow() && getExtraMoney()
  }

  def hasWonTheGameSmallRound(): Boolean = {
    return canChooseDyeAmount() && canRejectDyeTrow()
  }


  def activateCards(rollNum: Int,rollerId: Int, state: Gamestate) : Gamestate = {
    var tmpGamestate = state  //changes a lot so var
    for (p <- properties) {
      if(p.roleNumbers.contains(rollNum))
        if(rollerId == playerId || p.color == Blue||p.color == Red){
          tmpGamestate = p.activate(tmpGamestate)
        }
    }
    return tmpGamestate
  }
  def printAllCards(): Unit = {
    println("Your Current cards:")

    properties.foreach { c =>
      println(s"${c.cardName}:   ${c.description}")
    }
  }
    
    /*println(
    """
      |Cost:  Cards:            Dice number:
      |   1   Weizenfeld          1
      |   1   Bauernhof           2
      |   1   Bäckerei            2,3
      |   2   Cafe                3
      |   3   Wald                5
      |   6   Stadion             6
      |   6   Bürohaus            6
      |   7   Fernsehsender       6
      |   7   Molkerei            7
      |   3   Möbelfabrik         8
      |   6   Bergwerk            9
      |   3   Familien Restaurant 9,10
      |   3   Apfelgarten         10
      |   2   Markthalle          11,12
      |   22  Funkturm            
      |   16  Freizeitpark        
      |   4   Bahnhof             
      |   10 Einkaufszentrum      
      |   """.stripMargin)
    */

  
}