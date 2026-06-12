package de.htwg.se.machikoro.remake.model

import de.htwg.se.machikoro.remake.model.Type.*
import de.htwg.se.machikoro.remake.model.allCardsBaseGame.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AllCardsBaseGameSpec extends AnyWordSpec with Matchers {

  private def stateWithPlayers(): Gamestate =
    Gamestate(
      Players = List(
        Player(money = 10, playerId = 0),
        Player(money = 10, playerId = 1)
      ),
      CurrentTurnPlayerId = 0
    )

  "allCardsBaseGame" should {

    "define the starter cards correctly" in {
      starterweizenfeld.price shouldBe 0
      starterweizenfeld.cardName shouldBe "Weizenfeld"
      starterbaeckerei.price shouldBe 0
      starterbaeckerei.cardName shouldBe "Bäckerei"
    }

    "define regular card metadata correctly" in {
      weizenfeld.price shouldBe 1
      weizenfeld.cardType shouldBe Farm
      weizenfeld.color shouldBe Color.Blue
      weizenfeld.roleNumbers should contain(1)

      cafe.price shouldBe 2
      cafe.cardType shouldBe Restaurants
      cafe.color shouldBe Color.Red
      cafe.roleNumbers should contain(3)

      stadion.cardType shouldBe Major_Establishment
      stadion.color shouldBe Color.Purple
      stadion.roleNumbers should contain(6)

      funkturm.cardType shouldBe Landmark
      funkturm.color shouldBe Color.Yellow
      funkturm.price shouldBe 22
      funkturm.roleNumbers shouldBe empty
    }

    "execute simple bank payout effects" in {
      val state = stateWithPlayers()

      weizenfeld.effect(state, 0).Players.find(_.playerId == 0).get.money shouldBe 11
      bauernhof.effect(state, 0).Players.find(_.playerId == 0).get.money shouldBe 11
      baeckerei.effect(state, 0).Players.find(_.playerId == 0).get.money shouldBe 11
      minimarkt.effect(state, 0).Players.find(_.playerId == 0).get.money shouldBe 13
      wald.effect(state, 0).Players.find(_.playerId == 0).get.money shouldBe 11
      bergwerk.effect(state, 0).Players.find(_.playerId == 0).get.money shouldBe 15
      apfelgarten.effect(state, 0).Players.find(_.playerId == 0).get.money shouldBe 13
      fernsehsender.effect(state, 0).Players.find(_.playerId == 0).get.money shouldBe 11
    }

    "execute stadion effect" in {
      val result = stadion.effect(stateWithPlayers(), 0)

      result.Players.find(_.playerId == 0).get.money shouldBe 12
      result.Players.find(_.playerId == 1).get.money shouldBe 8
    }

    "execute cafe effect" in {
      val result = cafe.effect(stateWithPlayers(), 1)

      result.Players.find(_.playerId == 0).get.money shouldBe 9
      result.Players.find(_.playerId == 1).get.money shouldBe 11
    }

    "execute familien restaurant effect" in {
      val result = familienRestaurant.effect(stateWithPlayers(), 1)

      result.Players.find(_.playerId == 0).get.money shouldBe 8
      result.Players.find(_.playerId == 1).get.money shouldBe 12
    }

    "execute molkerei effect scaled by dairy cards" in {
      val player = Player(
        money = 0,
        playerId = 0,
        properties = List(
          bauernhof.copy(cardOwnerId = 0),
          bauernhof.copy(cardOwnerId = 0)
        )
      )

      val result = molkerei.effect(Gamestate(Players = List(player)), 0)

      result.Players.head.money shouldBe 6
    }

    "execute möbelfabrik effect scaled by industry cards" in {
      val player = Player(
        money = 0,
        playerId = 0,
        properties = List(
          wald.copy(cardOwnerId = 0),
          bergwerk.copy(cardOwnerId = 0)
        )
      )

      val result = möbelfabrik.effect(Gamestate(Players = List(player)), 0)

      result.Players.head.money shouldBe 6
    }

    "execute markthalle effect scaled by farm cards" in {
      val player = Player(
        money = 0,
        playerId = 0,
        properties = List(
          weizenfeld.copy(cardOwnerId = 0),
          apfelgarten.copy(cardOwnerId = 0)
        )
      )

      val result = markthalle.effect(Gamestate(Players = List(player)), 0)

      result.Players.head.money shouldBe 4
    }

    "leave state unchanged for not implemented or passive effects" in {
      val state = stateWithPlayers()

      buerohaus.effect(state, 0) shouldBe state
      funkturm.effect(state, 0) shouldBe state
      freizeitpark.effect(state, 0) shouldBe state
      bahnhof.effect(state, 0) shouldBe state
      einkaufszentrum.effect(state, 0) shouldBe state
    }
  }
}
