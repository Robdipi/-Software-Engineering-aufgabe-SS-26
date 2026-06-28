package AI_Generated_Tests

import de.htwg.se.machikoro.remake.model.*
import de.htwg.se.machikoro.remake.model.Data.AllCardsBaseGame.*
import de.htwg.se.machikoro.remake.model.Data.{Card, Gamestate, Player}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.ByteArrayOutputStream

class PlayerSpec extends AnyWordSpec with Matchers {

  private def moneyOf(state: Gamestate, playerId: Int): Int =
    state.Players.find(_.playerId == playerId).get.money

  "A Player" should {

    "detect landmark ownership" in {
      Player(properties = List(bahnhof.copy(cardOwnerId = 0)), playerId = 0).canChooseDyeAmount() shouldBe true
      Player(properties = List(freizeitpark.copy(cardOwnerId = 0)), playerId = 0).canGetAnotherTurn() shouldBe true
      Player(properties = List(funkturm.copy(cardOwnerId = 0)), playerId = 0).canRejectDyeTrow() shouldBe true
      Player(properties = List(einkaufszentrum.copy(cardOwnerId = 0)), playerId = 0).getExtraMoney() shouldBe true
    }

    "return false when landmarks are missing" in {
      val player = Player(playerId = 0)

      player.canChooseDyeAmount() shouldBe false
      player.canGetAnotherTurn() shouldBe false
      player.canRejectDyeTrow() shouldBe false
      player.getExtraMoney() shouldBe false
      player.hasWonTheGame() shouldBe false
      player.hasWonTheGameSmallRound() shouldBe false
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

    "reject full win condition when only one landmark is present" in {
      Player(properties = List(bahnhof.copy(cardOwnerId = 0)), playerId = 0).hasWonTheGame() shouldBe false
      Player(properties = List(freizeitpark.copy(cardOwnerId = 0)), playerId = 0).hasWonTheGame() shouldBe false
      Player(properties = List(funkturm.copy(cardOwnerId = 0)), playerId = 0).hasWonTheGame() shouldBe false
      Player(properties = List(einkaufszentrum.copy(cardOwnerId = 0)), playerId = 0).hasWonTheGame() shouldBe false
    }

    "reject full win condition when exactly one required landmark is missing" in {
      val missingBahnhof = Player(
        properties = List(
          freizeitpark.copy(cardOwnerId = 0),
          funkturm.copy(cardOwnerId = 0),
          einkaufszentrum.copy(cardOwnerId = 0)
        ),
        playerId = 0
      )

      val missingFreizeitpark = Player(
        properties = List(
          bahnhof.copy(cardOwnerId = 0),
          funkturm.copy(cardOwnerId = 0),
          einkaufszentrum.copy(cardOwnerId = 0)
        ),
        playerId = 0
      )

      val missingFunkturm = Player(
        properties = List(
          bahnhof.copy(cardOwnerId = 0),
          freizeitpark.copy(cardOwnerId = 0),
          einkaufszentrum.copy(cardOwnerId = 0)
        ),
        playerId = 0
      )

      val missingEinkaufszentrum = Player(
        properties = List(
          bahnhof.copy(cardOwnerId = 0),
          freizeitpark.copy(cardOwnerId = 0),
          funkturm.copy(cardOwnerId = 0)
        ),
        playerId = 0
      )

      missingBahnhof.hasWonTheGame() shouldBe false
      missingFreizeitpark.hasWonTheGame() shouldBe false
      missingFunkturm.hasWonTheGame() shouldBe false
      missingEinkaufszentrum.hasWonTheGame() shouldBe false
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

    "reject small round win condition when only one required landmark is present" in {
      Player(properties = List(bahnhof.copy(cardOwnerId = 0)), playerId = 0).hasWonTheGameSmallRound() shouldBe false
      Player(properties = List(funkturm.copy(cardOwnerId = 0)), playerId = 0).hasWonTheGameSmallRound() shouldBe false
    }

    "activate matching blue cards even when another player rolled" in {
      val player = Player(
        money = 0,
        properties = List(weizenfeld.copy(cardOwnerId = 0)),
        playerId = 0
      )

      val result = player.activateCards(1, 1, Gamestate(Players = List(player)))

      moneyOf(result, 0) shouldBe 1
    }

    "activate matching green cards only when owner rolled" in {
      val player = Player(
        money = 0,
        properties = List(baeckerei.copy(cardOwnerId = 0)),
        playerId = 0
      )
      val game = Gamestate(Players = List(player))

      player.activateCards(2, 0, game).Players.head.money shouldBe 1
      player.activateCards(2, 1, game).Players.head.money shouldBe 0
    }

    "activate matching red cards when another player rolled" in {
      val owner = Player(
        money = 0,
        properties = List(cafe.copy(cardOwnerId = 0)),
        playerId = 0
      )
      val roller = Player(money = 5, playerId = 1)

      val game = Gamestate(
        Players = List(owner, roller),
        CurrentTurnPlayerId = 1
      )

      val result = owner.activateCards(3, 1, game)

      moneyOf(result, 0) shouldBe 1
      moneyOf(result, 1) shouldBe 4
    }

    "not activate non matching cards" in {
      val player = Player(
        money = 0,
        properties = List(weizenfeld.copy(cardOwnerId = 0)),
        playerId = 0
      )

      player.activateCards(2, 0, Gamestate(Players = List(player))).Players.head.money shouldBe 0
    }


    "not activate matching red cards when their owner rolled" in {
      val owner = Player(money = 0, properties = List(cafe.copy(cardOwnerId = 0)), playerId = 0)
      val game = Gamestate(Players = List(owner), CurrentTurnPlayerId = 0)

      owner.activateCards(3, 0, game) shouldBe game
    }



    "print all cards with header and card descriptions" in {
      val player = Player(
        properties = List(weizenfeld.copy(cardOwnerId = 0)),
        playerId = 0
      )
      val printed = player.printAllCards()
      printed should include("Your Current cards:")
      printed should include("Weizenfeld")
      printed should include("Erhalte 1 Münze aus der Bank.")
    }
  }
}
