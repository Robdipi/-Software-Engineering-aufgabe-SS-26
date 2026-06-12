package de.htwg.se.machikoro.remake.model

import de.htwg.se.machikoro.remake.model.allCardsBaseGame.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GamestateSpec extends AnyWordSpec with Matchers {

  private val p1 = Player(money = 10, playerId = 0)
  private val p2 = Player(money = 10, playerId = 1)

  "Gamestate" should {

    "change money of a player" in {
      val result = Gamestate(Players = List(p1, p2)).changeMoneyOfPlayer(0, 5)

      result.Players.find(_.playerId == 0).get.money shouldBe 15
      result.Players.find(_.playerId == 1).get.money shouldBe 10
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
      val result = Gamestate(Players = List(p1, p2)).transferMoneyBetweenPlayers(0, 1, 3)

      result.Players.find(_.playerId == 0).get.money shouldBe 7
      result.Players.find(_.playerId == 1).get.money shouldBe 13
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
      val result = Gamestate(Players = List(giver, taker))
        .transferMoneyBetweenPlayers(0, 1, 2, Type.Restaurants)

      result.Players.find(_.playerId == 0).get.money shouldBe 7
      result.Players.find(_.playerId == 1).get.money shouldBe 13
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

      val result = Gamestate(Players = List(player))
        .changeMoneyOfPlayerScaleByType(0, Type.Farm, 2)

      result.Players.head.money shouldBe 4
    }

    "give a card to a player and set the owner id" in {
      val result = Gamestate(Players = List(p1)).giveCard(0, weizenfeld)

      result.Players.head.properties.size shouldBe 1
      result.Players.head.properties.head.cardOwnerId shouldBe 0
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

    "keep the current player when GetsAnotherTurn is true" in {
      val player = Player(money = 10, playerId = 0, GetsAnotherTurn = true)
      val state = Gamestate(Players = List(player, p2), CurrentTurnPlayerId = 0, curentTurn = 0)

      val result = state.iterateTurn()

      result.curentTurn shouldBe 1
      result.CurrentTurnPlayerId shouldBe 0
      result.Players.find(_.playerId == 0).get.GetsAnotherTurn shouldBe false
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

    "remove a card from stack" in {
      val state = Gamestate(Players = List(p1), cardStacks = List(cardStack(5, weizenfeld)))

      val result = state.removeCardFromStack(weizenfeld)

      result.cardStacks.head.amount shouldBe 4
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
