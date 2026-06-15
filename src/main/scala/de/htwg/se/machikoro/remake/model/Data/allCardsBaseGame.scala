package de.htwg.se.machikoro.remake.model.Data

import Type.*
import de.htwg.se.machikoro.remake.model.Data.{Card, Color, Type}

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
  val starterweizenfeld = Card(
    cardName = "Weizenfeld",
    price = 0,
    cardType = Type.Farm,
    roleNumbers = Array(1),
    color = Color.Blue,
    description = "Erhalte 1 Münze aus der Bank.",
    texturePath = "Assets/textures/cards/weizenfeld.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.changeMoneyOfPlayer(Owner, 1)
    }
  )
  val weizenfeld = Card(
    cardName = "Weizenfeld",
    price = 1,
    cardType = Type.Farm,
    roleNumbers = Array(1),
    color = Color.Blue,
    description =  "Erhalte 1 Münze aus der Bank.",
    texturePath = "Assets/textures/cards/weizenfeld.png",
    cardOwnerId = -1,
      effect = (gamestate, Owner) => {
        gamestate.changeMoneyOfPlayer(Owner,1)
    }
  )
  val bauernhof = Card(
    cardName = "Bauernhof",
    price = 1,
    cardType = Type.Dairy,
    roleNumbers = Array(2),
    color = Color.Blue,
    description = "Erhalte 1 Münze aus der Bank.",
    texturePath = "Assets/textures/cards/bauernhof.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.changeMoneyOfPlayer(Owner, 1)
    }
  )
  val baeckerei = Card(
    cardName = "Bäckerei",
    price = 1,
    cardType = Type.Store,
    roleNumbers = Array(2,3),
    color = Color.Green,
    description = "Erhalte 1 Münze aus der Bank.",
    texturePath = "Assets/textures/cards/baeckerei.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.changeMoneyOfPlayer(Owner, 1)
    }
  )
  val starterbaeckerei = Card(
    cardName = "Bäckerei",
    price = 0,
    cardType = Type.Store,
    roleNumbers = Array(2, 3),
    color = Color.Green,
    description = "Erhalte 1 Münze aus der Bank.",
    texturePath = "Assets/textures/cards/baeckerei.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.changeMoneyOfPlayer(Owner, 1)
    }
  )
  val cafe = Card(
    cardName = "Cafe",
    price = 2,
    cardType = Type.Restaurants,
    roleNumbers = Array(3),
    color = Color.Red,
    description = "Erhalte 1 Münze von dem Mitspieler der eine '3' gewürfelt hat.",
    texturePath = "Assets/textures/cards/cafe.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.transferMoneyBetweenPlayers(gamestate.CurrentTurnPlayerId,Owner,1,Restaurants)//Transfer Money from the Player who has gotten the number to the player who owns this card
    }
  )
  val minimarkt = Card(
    cardName = "Mini-Markt",
    price = 2,
    cardType = Type.Store,
    roleNumbers = Array(4),
    color = Color.Green,
    description = "Erhalte 3 Münzen aus der Bank.",
    texturePath = "Assets/textures/cards/minimarkt.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.changeMoneyOfPlayer(Owner, 3)
    }
  )
  val wald = Card(
    cardName = "wald",
    price = 3,
    cardType = Type.Industry,
    roleNumbers = Array(5),
    color = Color.Blue,
    description = "Erhalte 1 Münze aus der Bank.",
    texturePath = "Assets/textures/cards/forrest.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.changeMoneyOfPlayer(Owner, 1)
    }
  )
  val stadion = Card(
    cardName = "stadion",
    price = 6,
    cardType = Type.Major_Establishment,
    roleNumbers = Array(6),
    color = Color.Purple,
    description = "Erhalte von jedem Mitspieler 2 Münzen.",
    texturePath = "Assets/textures/cards/stadium.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.stealFromEveryone(Owner, 2)
    }
  )
  
  val buerohaus = Card(
    cardName = "Bürohaus",
    price = 6,
    cardType = Type.Major_Establishment,
    roleNumbers = Array(6),
    color = Color.Purple,
    description = "Tausche 1 karte mit einem Mitspieler deiner Wahl. Kein >🗼< Unternehmen.",
    texturePath = "Assets/textures/cards/buero.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate//TODO
    }
  )
  val fernsehsender = Card(
    cardName = "Fernsehsender",
    price = 7,
    cardType = Type.Major_Establishment,
    roleNumbers = Array(6),
    color = Color.Purple,
    description = "Erhalte von einem Mitspieler deiner Wahl 5 Münzen.",
    texturePath = "Assets/textures/cards/fernsehsender.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.changeMoneyOfPlayer(Owner, 1)
    }
  )

  val molkerei = Card(
    cardName = "Molkerei",
    price = 7,
    cardType = Type.Secondary_Industry, 
    roleNumbers = Array(7),
    color = Color.Green,
    description = "Erhalte 3 Münzen aus der Bank für jedes deiner >🐄< Unternehmen",
    texturePath = "Assets/textures/cards/molkerei.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.changeMoneyOfPlayerScaleByType(Owner, Type.Dairy, 3)
    }
  )

  val möbelfabrik = Card(
    cardName = "Möbelfabrik",
    price = 3,
    cardType = Type.Secondary_Industry,
    roleNumbers = Array(8),
    color = Color.Green,
    description = "Erhalte 3 Münzen aus der Bank für jedes deiner >⚙️< Unternehmen.",
    texturePath = "Assets/textures/cards/saegewerk.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.changeMoneyOfPlayerScaleByType(Owner,Type.Industry,3)
    }
  )

  val bergwerk = Card(
    cardName = "Bergwerk",
    price = 6,
    cardType = Type.Industry,
    roleNumbers = Array(9),
    color = Color.Blue,
    description = "Erhalte 5 Münzen aus der Bank.",
    texturePath = "Assets/textures/cards/bergwerk.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.changeMoneyOfPlayer(Owner, 5)
    }
  )

  val familienRestaurant = Card(
    cardName = "Familien-Restaurant",
    price = 3,
    cardType = Type.Restaurants,
    roleNumbers = Array(9, 10),
    color = Color.Red,
    description = "Erhalte 2 Münzen von dem Mitspieler, der eine 9 oder 10 gewürfelt hat.",
    texturePath = "Assets/textures/cards/familyRestaurant.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.transferMoneyBetweenPlayers(gamestate.CurrentTurnPlayerId, Owner, 2, Restaurants)
    }
  )
  val apfelgarten = Card(
    cardName = "apfelgarten",
    price = 3,
    cardType = Type.Farm,
    roleNumbers = Array(10),
    color = Color.Blue,
    description = "Erhalte 3 Münzen aus der Bank.",
    texturePath = "Assets/textures/cards/apfelhein.png",
    cardOwnerId = -1,
    effect = (gamestate, Owner) => {
      gamestate.changeMoneyOfPlayer(Owner,3)
    }
  )
  val markthalle = Card(
    cardName = "Markthalle",
    price = 2,
    cardType = Type.Secondary_Industry,
    roleNumbers = Array(11,12),
    color = Color.Green,
    description = "Erhalte 2 Münzen aus der Bank für jedes deiner >🌾< Unternehmen.",
    texturePath = "Assets/textures/cards/markthalle.png",
    cardOwnerId = -1,
    effect = (gamestate, OwnerID) => {
      gamestate.changeMoneyOfPlayerScaleByType(OwnerID, Type.Farm, 2)
    }
  )
  val funkturm = Card(
    cardName = "Funkturm",
    price = 22,
    cardType = Type.Landmark,
    roleNumbers = Array(),
    color = Color.Yellow,
    description = "Einmal pro Zug darfst du erneut würfeln.",
    texturePath = "Assets/textures/cards/funkturm.png",
    cardOwnerId = -1,
    effect = (gamestate, OwnerID) => {
      gamestate
    }
  )
 
  val freizeitpark = Card(
    cardName = "Freizeitpark",
    price = 16,
    cardType = Type.Landmark,
    roleNumbers = Array(),
    color = Color.Yellow,
    description = "Würfelst du zwei gleiche Zahlen, hast du einen weiteren Zug.",
    texturePath = "Assets/textures/cards/freizeitpark.png",
    cardOwnerId = -1,
    effect = (gamestate, OwnerID) => {
      gamestate
    }
  )

  val bahnhof = Card(
    cardName = "Bahnhof",
    price = 4,
    cardType = Type.Landmark,
    roleNumbers = Array(),
    color = Color.Yellow,
    description = "Würfle mit 1 oder 2 Würfeln.",
    texturePath = "Assets/textures/cards/bahnhof.png",
    cardOwnerId = -1,
    effect = (gamestate, OwnerID) => {
      gamestate
    }
  )

  val einkaufszentrum = Card(
    cardName = "Einkaufszentrum",
    price = 10,
    cardType = Type.Landmark,
    roleNumbers = Array(),
    color = Color.Yellow,
    description = "Erhalte 1 Münze mehr für jedes deiner >☕️< und >🛍️< Unternehmen.",
    texturePath = "Assets/textures/cards/einkaufszentrum.png",
    cardOwnerId = -1,
    effect = (gamestate, OwnerID) => {
      gamestate
    }
  )

}
