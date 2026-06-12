package de.htwg.se.machikoro.remake.model

import de.htwg.se.machikoro.remake.model.allCardsBaseGame.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" should {

    "detect missing special buildings" in {
      val player = Player(playerId = 0)

      player.canChooseDyeAmount() shouldBe false
      player.canGetAnotherTurn() shouldBe false
      player.canRejectDyeTrow() shouldBe false
      player.getExtraMoney() shouldBe false
    }

    "detect Bahnhof ownership" in {
      Player(properties = List(bahnhof.copy(cardOwnerId = 0)), playerId = 0)
        .canChooseDyeAmount() shouldBe true
    }

    "detect Freizeitpark ownership" in {
      Player(properties = List(freizeitpark.copy(cardOwnerId = 0)), playerId = 0)
        .canGetAnotherTurn() shouldBe true
    }

    "detect Funkturm ownership" in {
      Player(properties = List(funkturm.copy(cardOwnerId = 0)), playerId = 0)
        .canRejectDyeTrow() shouldBe true
    }

    "detect Einkaufszentrum ownership" in {
      Player(properties = List(einkaufszentrum.copy(cardOwnerId = 0)), playerId = 0)
        .getExtraMoney() shouldBe true
    }

    "detect a full win condition" in {
      val player = Player(
        properties = List(
          bahnhof.copy(cardOwnerId = 0),
          freizeitpark.copy(cardOwnerId = 0),
          funkturm.copy(cardOwnerId = 0),
          einkaufszentrum.copy(cardOwnerId = 0)
        ),
        playerId = 0
      )

      player.hasWonTheGame() shouldBe true
    }

    "detect a small round win condition" in {
      val player = Player(
        properties = List(
          bahnhof.copy(cardOwnerId = 0),
          funkturm.copy(cardOwnerId = 0)
        ),
        playerId = 0
      )

      player.hasWonTheGameSmallRound() shouldBe true
    }

    "not detect a win condition when a landmark is missing" in {
      val player = Player(
        properties = List(
          bahnhof.copy(cardOwnerId = 0),
          freizeitpark.copy(cardOwnerId = 0),
          funkturm.copy(cardOwnerId = 0)
        ),
        playerId = 0
      )

      player.hasWonTheGame() shouldBe false
    }

    "activate matching blue cards even when another player rolled" in {
      val player0 = Player(
        money = 0,
        properties = List(weizenfeld.copy(cardOwnerId = 0)),
        playerId = 0
      )
      val player1 = Player(money = 0, playerId = 1)
      val game = Gamestate(Players = List(player0, player1), CurrentTurnPlayerId = 1)

      val result = player0.activateCards(1, 1, game)

      result.Players.find(_.playerId == 0).get.money shouldBe 1
    }

    "activate matching green cards when the owner rolled" in {
      val player = Player(
        money = 0,
        properties = List(baeckerei.copy(cardOwnerId = 0)),
        playerId = 0
      )
      val game = Gamestate(Players = List(player), CurrentTurnPlayerId = 0)

      val result = player.activateCards(2, 0, game)

      result.Players.head.money shouldBe 1
    }

    "not activate matching green cards when another player rolled" in {
      val player0 = Player(
        money = 0,
        properties = List(baeckerei.copy(cardOwnerId = 0)),
        playerId = 0
      )
      val player1 = Player(money = 0, playerId = 1)
      val game = Gamestate(Players = List(player0, player1), CurrentTurnPlayerId = 1)

      val result = player0.activateCards(2, 1, game)

      result.Players.find(_.playerId == 0).get.money shouldBe 0
    }

    "activate matching red cards" in {
      val player0 = Player(money = 10, playerId = 0)
      val player1 = Player(
        money = 10,
        properties = List(cafe.copy(cardOwnerId = 1)),
        playerId = 1
      )
      val game = Gamestate(Players = List(player0, player1), CurrentTurnPlayerId = 0)

      val result = player1.activateCards(3, 0, game)

      result.Players.find(_.playerId == 0).get.money shouldBe 9
      result.Players.find(_.playerId == 1).get.money shouldBe 11
    }

    "not activate non matching cards" in {
      val player = Player(
        money = 0,
        properties = List(weizenfeld.copy(cardOwnerId = 0)),
        playerId = 0
      )
      val game = Gamestate(Players = List(player))

      val result = player.activateCards(2, 0, game)

      result.Players.head.money shouldBe 0
    }
  }
}
