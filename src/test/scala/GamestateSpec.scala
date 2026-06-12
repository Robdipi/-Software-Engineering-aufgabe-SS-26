package de.htwg.se.machikoro.remake.model

import de.htwg.se.machikoro.remake.model.allCardsBaseGame.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GamestateSpec extends AnyWordSpec with Matchers {

  private val p1 = Player(money = 10, playerId = 0)
  private val p2 = Player(money = 10, playerId = 1)
  private val p3 = Player(money = 10, playerId = 2)

  "Gamestate" should {

    "expose all turn states" in {
      turnState.values.toList should contain allOf (
        turnState.StartofTurn,
        turnState.ChooseDiceAmount,
        turnState.Result1,
        turnState.AskForRejectionOfResult,
        turnState.Result2,
        turnState.Cardeffects,
        turnState.Buyphase,
        turnState.EndofTurn,
        turnState.PlayerWins,
        turnState.ALREADY_OWN_THAT_YELLOW_CARD_WARNING,
        turnState.ALREADY_OWN_PURPLE_CARD_WARNING,
        turnState.NO_CARDS_LEFT_OF_THAT_TYPE_WARNING,
        turnState.YOU_CANT_AFFORD_THIS_WARNING,
        turnState.NONE_EXISTANT_CARDNAME_WARNING
      )
    }

    "create a card stack" in {
      val stack = cardStack(5, weizenfeld)

      stack.amount shouldBe 5
      stack.stackCard shouldBe weizenfeld
    }

    "change money of a player" in {
      val result = Gamestate(Players = List(p1, p2)).changeMoneyOfPlayer(0, 5)

      result.Players.find(_.playerId == 0).get.money shouldBe 15
      result.Players.find(_.playerId == 1).get.money shouldBe 10
    }

    "leave all players unchanged when changing money for an unknown player" in {
      val state = Gamestate(Players = List(p1, p2))

      state.changeMoneyOfPlayer(99, 5) shouldBe state
    }

    "add one extra coin for store cards when the player owns Einkaufszentrum" in {
      val player = Player(
        money = 10,
        properties = List(einkaufszentrum.copy(cardOwnerId = 0)),
        playerId = 0
      )
      val result = Gamestate(Players = List(player)).changeMoneyOfPlayer(0, 3, Type.Store)

      result.Players.head.money shouldBe 14
    }

    "not add extra money for non store cards" in {
      val player = Player(
        money = 10,
        properties = List(einkaufszentrum.copy(cardOwnerId = 0)),
        playerId = 0
      )
      val result = Gamestate(Players = List(player)).changeMoneyOfPlayer(0, 3, Type.Farm)

      result.Players.head.money shouldBe 13
    }

    "transfer money between players" in {
      val result = Gamestate(Players = List(p1, p2, p3)).transferMoneyBetweenPlayers(0, 1, 3)

      result.Players.find(_.playerId == 0).get.money shouldBe 7
      result.Players.find(_.playerId == 1).get.money shouldBe 13
      result.Players.find(_.playerId == 2).get.money shouldBe 10
    }

    "not transfer money to self" in {
      val state = Gamestate(Players = List(p1))

      state.transferMoneyBetweenPlayers(0, 0, 5) shouldBe state
    }

    "add one extra coin for restaurant cards when the taker owns Einkaufszentrum" in {
      val giver = Player(money = 10, playerId = 0)
      val taker = Player(
        money = 10,
        properties = List(einkaufszentrum.copy(cardOwnerId = 1)),
        playerId = 1
      )
      val observer = Player(money = 10, playerId = 2)
      val result = Gamestate(Players = List(giver, taker, observer))
        .transferMoneyBetweenPlayers(0, 1, 2, Type.Restaurants)

      result.Players.find(_.playerId == 0).get.money shouldBe 7
      result.Players.find(_.playerId == 1).get.money shouldBe 13
      result.Players.find(_.playerId == 2).get.money shouldBe 10
    }

    "steal from everyone" in {
      val result = Gamestate(Players = List(p1, p2)).stealFromEveryone(0, 2)

      result.Players.find(_.playerId == 0).get.money shouldBe 12
      result.Players.find(_.playerId == 1).get.money shouldBe 8
    }

    "scale money by card type" in {
      val player = Player(
        money = 0,
        properties = List(
          weizenfeld.copy(cardOwnerId = 0),
          apfelgarten.copy(cardOwnerId = 0),
          cafe.copy(cardOwnerId = 0)
        ),
        playerId = 0
      )

      val result = Gamestate(Players = List(player, p2))
        .changeMoneyOfPlayerScaleByType(0, Type.Farm, 2)

      result.Players.find(_.playerId == 0).get.money shouldBe 4
      result.Players.find(_.playerId == 1).get.money shouldBe 10
    }

    "leave state unchanged when scaling money for an unknown player" in {
      val state = Gamestate(Players = List(p1, p2))

      state.changeMoneyOfPlayerScaleByType(99, Type.Farm, 2) shouldBe state
    }

    "give a card to a player and set the owner id" in {
      val result = Gamestate(Players = List(p1, p2)).giveCard(0, weizenfeld)

      result.Players.find(_.playerId == 0).get.properties.size shouldBe 1
      result.Players.find(_.playerId == 0).get.properties.head.cardOwnerId shouldBe 0
      result.Players.find(_.playerId == 1).get.properties shouldBe empty
    }

    "leave players unchanged when giving a card to an unknown player" in {
      val state = Gamestate(Players = List(p1, p2))

      state.giveCard(99, weizenfeld) shouldBe state
    }

    "activate cards for all players" in {
      val player0 = Player(
        money = 0,
        properties = List(weizenfeld.copy(cardOwnerId = 0)),
        playerId = 0
      )
      val player1 = Player(
        money = 0,
        properties = List(weizenfeld.copy(cardOwnerId = 1)),
        playerId = 1
      )
      val state = Gamestate(Players = List(player0, player1), CurrentTurnPlayerId = 0)

      val result = state.activateCards(1, 0)

      result.Players.find(_.playerId == 0).get.money shouldBe 1
      result.Players.find(_.playerId == 1).get.money shouldBe 1
    }

    "iterate to the next player normally" in {
      val state = Gamestate(Players = List(p1, p2), CurrentTurnPlayerId = 0, curentTurn = 0)

      val result = state.iterateTurn()

      result.curentTurn shouldBe 1
      result.CurrentTurnPlayerId shouldBe 1
    }

    "wrap turn iteration back to the first player" in {
      val state = Gamestate(Players = List(p1, p2), CurrentTurnPlayerId = 1, curentTurn = 3)

      val result = state.iterateTurn()

      result.curentTurn shouldBe 4
      result.CurrentTurnPlayerId shouldBe 0
    }

    "keep the current player when GetsAnotherTurn is true" in {
      val player = Player(money = 10, playerId = 0, GetsAnotherTurn = true)
      val state = Gamestate(Players = List(player, p2), CurrentTurnPlayerId = 0, curentTurn = 0)

      val result = state.iterateTurn()

      result.curentTurn shouldBe 1
      result.CurrentTurnPlayerId shouldBe 0
      result.Players.find(_.playerId == 0).get.GetsAnotherTurn shouldBe false
      result.Players.find(_.playerId == 1).get.money shouldBe 10
    }

    "detect whether the current player has won" in {
      val winner = Player(
        playerId = 0,
        properties = List(
          bahnhof.copy(cardOwnerId = 0),
          freizeitpark.copy(cardOwnerId = 0),
          funkturm.copy(cardOwnerId = 0),
          einkaufszentrum.copy(cardOwnerId = 0)
        )
      )

      Gamestate(Players = List(winner), CurrentTurnPlayerId = 0).currentPlayerHasWon() shouldBe true
    }

    "detect when the current player has not won" in {
      Gamestate(Players = List(p1), CurrentTurnPlayerId = 0).currentPlayerHasWon() shouldBe false
    }

    "return false for win check when the current player does not exist" in {
      Gamestate(Players = List(p1), CurrentTurnPlayerId = 99).currentPlayerHasWon() shouldBe false
    }

    "remove a card from stack" in {
      val state = Gamestate(Players = List(p1), cardStacks = List(cardStack(5, weizenfeld), cardStack(2, cafe)))

      val result = state.removeCardFromStack(weizenfeld)

      result.cardStacks.find(_.stackCard.cardName == weizenfeld.cardName).get.amount shouldBe 4
      result.cardStacks.find(_.stackCard.cardName == cafe.cardName).get.amount shouldBe 2
    }

    "leave stacks unchanged when removing a non existing card" in {
      val state = Gamestate(Players = List(p1), cardStacks = List(cardStack(5, weizenfeld)))

      state.removeCardFromStack(cafe) shouldBe state
    }

    "change state" in {
      Gamestate().changeState(turnState.Buyphase).state shouldBe turnState.Buyphase
    }

    "change dice amount" in {
      Gamestate().changeDiceChosen(2).diceChoosen shouldBe 2
    }

    "change dice result" in {
      Gamestate().changeDiceResult(9).DiceResult shouldBe 9
    }

    "change players" in {
      Gamestate().changePlayers(List(p1)).Players should contain(p1)
    }
  }
}
