package de.htwg.se.machikoro.remake

import de.htwg.se.machikoro.remake.Type.Industry

//cardType:
//Major_Establishment => Fernsehturn-Symbol
//Landmark => Fernsehturm-Symbol
//Farm => Getreide-Symbol
//Industry => Zahnrad-Symbol
//Secondary_Industry => Fabrik- & Abrissbirne-Symbol
//Restaurants => Kaffeetasse-Symbol
//Store => marktstand-Symbol
//Dairy => Kuh-Symbol

object allCardsBaseGame {
  val starterweizenfeld = card(
    cardName = "Weizenfeld",
    price = 0,
    cardType = Type.Farm,
    roleNumbers = Array(1),
    color = Color.Blue,
    description = "erhalte 1 Münze aus der Bank.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.changeMoneyOfPlayer(OwnerID, 1)
    }
  )
  val weizenfeld = card(
    cardName = "Weizenfeld",
    price = 1,
    cardType = Type.Farm,
    roleNumbers = Array(1),
    color = Color.Blue,
    description =  "erhalte 1 Münze aus der Bank.",
    texturePath = "",
    cardOwnerID = -1,
      effect = (gamestate, OwnerID) => {
        gamestate.changeMoneyOfPlayer(OwnerID,1)
    }
  )
  val bauernhof = card(
    cardName = "Bauernhof",
    price = 1,
    cardType = Type.Dairy,
    roleNumbers = Array(2),
    color = Color.Blue,
    description = "erhalte 1 Münze aus der Bank.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.changeMoneyOfPlayer(OwnerID, 1)
    }
  )
  val baeckerei = card(
    cardName = "Bäckerei",
    price = 1,
    cardType = Type.Store,
    roleNumbers = Array(2,3),
    color = Color.Green,
    description = "erhalte 1 Münze aus der Bank.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.changeMoneyOfPlayer(OwnerID, 1)
    }
  )
  val starterbaeckerei = card(
    cardName = "Bäckerei",
    price = 0,
    cardType = Type.Store,
    roleNumbers = Array(2, 3),
    color = Color.Green,
    description = "erhalte 1 Münze aus der Bank.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.changeMoneyOfPlayer(OwnerID, 1)
    }
  )
  val cafe = card(
    cardName = "Cafe",
    price = 2,
    cardType = Type.Restaurants,
    roleNumbers = Array(3),
    color = Color.Red,
    description = "erhalte 1 Münze von dem Mitspieler der eine '3' gewürfelt hat.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.transferMoneyBetweenPlayers(gamestate.CurrentTurnPlayerID,OwnerID,1)//Transfer Money from the Player who has gotten the number to the player who owns this card
    }
  )
  val minimarkt = card(
    cardName = "Mini-Markt",
    price = 2,
    cardType = Type.Store,
    roleNumbers = Array(4),
    color = Color.Green,
    description = "Erhalte 3 Münzen aus der Bank.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.changeMoneyOfPlayer(OwnerID, 3)
    }
  )
  val wald = card(
    cardName = "wald",
    price = 3,
    cardType = Type.Industry,
    roleNumbers = Array(5),
    color = Color.Blue,
    description = "Erhalte 1 Münze aus der Bank.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.changeMoneyOfPlayer(OwnerID, 1)
    }
  )
  val stadion = card(
    cardName = "stadion",
    price = 6,
    cardType = Type.Major_Establishment,
    roleNumbers = Array(6),
    color = Color.Purple,
    description = "Erhalte von jedem Mitspieler 2 Münzen.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.changeMoneyOfPlayer(OwnerID, 1)
    }
  )
  
  val buerohaus = card(
    cardName = "Bürohaus",
    price = 6,
    cardType = Type.Major_Establishment,
    roleNumbers = Array(6),
    color = Color.Purple,
    description = "Tausche 1 karte mit einem Mitspieler deiner Wahl. Kein >🗼< Unternehmen.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate//TODO
    }
  )
  val fernsehsender = card(
    cardName = "Fernsehsender",
    price = 7,
    cardType = Type.Major_Establishment,
    roleNumbers = Array(6),
    color = Color.Purple,
    description = "Erhalte von einem Mitspieler deiner Wahl 5 Münzen.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.changeMoneyOfPlayer(OwnerID, 1)
    }
  )

  val molkerei = card(
    cardName = "Molkerei",
    price = 7,
    cardType = Type.Secondary_Industry, 
    roleNumbers = Array(7),
    color = Color.Green,
    description = "Erhalte 3 Münzen aus der Bank für jedes deiner >🐄< Unternehmen",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.changeMoneyOfPlayerScaleByType(OwnerID, Type.Dairy, 3)
    }
  )

  val möbelfabrik = card(
    cardName = "Möbelfabrik",
    price = 3,
    cardType = Type.Secondary_Industry,
    roleNumbers = Array(8),
    color = Color.Green,
    description = "Erhalte 3 Münzen aus der Bank für jedes deiner >⚙️< Unternehmen.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.changeMoneyOfPlayerScaleByType(OwnerID,Type.Industry,3)
    }
  )

  val bergwerk = card(
    cardName = "Bergwerk",
    price = 6,
    cardType = Type.Industry,
    roleNumbers = Array(9),
    color = Color.Blue,
    description = "Erhalte 5 Münzen aus der Bank.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.changeMoneyOfPlayer(OwnerID, 5)
    }
  )

  val familienRestaurant = card(
    cardName = "Familien-Restaurant",
    price = 3,
    cardType = Type.Restaurants,
    roleNumbers = Array(9, 10),
    color = Color.Red,
    description = "Erhalte 2 Münzen von dem Mitspieler, der eine 9 oder 10 gewürfelt hat.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.transferMoneyBetweenPlayers(gamestate.CurrentTurnPlayerID, OwnerID, 2)
    }
  )

  val markthalle = card(
    cardName = "Markthalle",
    price = 2,
    cardType = Type.Secondary_Industry,
    roleNumbers = Array(11,12),
    color = Color.Green,
    description = "Erhalte 2 Münzen aus der Bank für jedes deiner >🌾< Unternehmen.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.changeMoneyOfPlayerScaleByType(OwnerID, Type.Farm, 2)
    }
  )
  val funkturm = card(
    cardName = "Funkturm",
    price = 22,
    cardType = Type.Landmark,
    roleNumbers = Array(11, 12),
    color = Color.Yellow,
    description = "Einmal pro Zug darfst du erneut würfeln.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate
    }
  )
 
  val freizeitpark = card(
    cardName = "Freizeitpark",
    price = 16,
    cardType = Type.Landmark,
    roleNumbers = Array(),
    color = Color.Yellow,
    description = "Würfelst du zwei gleiche Zahlen, hast du einen weiteren Zug.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate
    }
  )

  val bahnhof = card(
    cardName = "Bahnhof",
    price = 4,
    cardType = Type.Landmark,
    roleNumbers = Array(),
    color = Color.Yellow,
    description = "Würfle mit 1 oder 2 Würfeln.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate
    }
  )

  val einkaufszentrum = card(
    cardName = "Einkaufszentrum",
    price = 10,
    cardType = Type.Landmark,
    roleNumbers = Array(),
    color = Color.Yellow,
    description = "Erhalte 1 Münze mehr für jedes deiner >☕️< und >🛍️< Unternehmen.",
    texturePath = "",
    cardOwnerID = -1,
    effect = (gamestate, OwnerID) => {
      gamestate
    }
  )

}
