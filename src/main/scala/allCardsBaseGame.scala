package de.htwg.se.machikoro.remake

object allCardsBaseGame {


  val starterweizenfeld = card(
    cardName = "Weizenfeld",
    price = 0,
    cardType = Type.Farm,
    roleNumbers = Array(1),
    color = Color.Blue,
    description = "erhalte 1 Münze aus der Bank",
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
    description =  "erhalte 1 Münze aus der Bank",
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
    description = "erhalte 1 Münze aus der Bank",
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
    description = "erhalte 1 Münze aus der Bank",
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
    description = "erhalte 1 Münze aus der Bank",
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
}
