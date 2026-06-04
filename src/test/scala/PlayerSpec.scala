package de.htwg.se.machikoro.remake.model

import de.htwg.se.machikoro.remake.model.allCardsBaseGame._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" should {

    "detect Bahnhof ownership" in {
      Player(
        money = 0,
        properties = List(bahnhof.copy(cardOwnerId = 0)),
        playerId = 0
      ).canChooseDyeAmount() shouldBe true
    }

    "detect Freizeitpark ownership" in {
      Player(
        properties = List(freizeitpark.copy(cardOwnerId = 0)),
        playerId = 0
      ).canGetAnotherTurn() shouldBe true
    }

    "detect Funkturm ownership" in {
      Player(
        properties = List(funkturm.copy(cardOwnerId = 0)),
        playerId = 0
      ).canRejectDyeTrow() shouldBe true
    }

    "detect Einkaufszentrum ownership" in {
      Player(
        properties = List(einkaufszentrum.copy(cardOwnerId = 0)),
        playerId = 0
      ).getExtraMoney() shouldBe true
    }

    "detect a win condition" in {
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

    "activate matching cards" in {
      val wheat = allCardsBaseGame.weizenfeld.copy(cardOwnerId = 0)

      val player = Player(
        money = 0,
        properties = List(wheat),
        playerId = 0
      )

      val game = Gamestate(Players = List(player))

      val result = player.activateCards(1, 0, game)

      result.Players.head.money shouldBe 1
    }

    "not activate non matching cards" in {
      val wheat = allCardsBaseGame.weizenfeld.copy(cardOwnerId = 0)

      val player = Player(
        money = 0,
        properties = List(wheat),
        playerId = 0
      )

      val game = Gamestate(Players = List(player))

      val result = player.activateCards(2, 0, game)

      result.Players.head.money shouldBe 0
    }
  }
}