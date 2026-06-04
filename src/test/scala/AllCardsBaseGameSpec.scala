package de.htwg.se.machikoro.remake.model

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

    "define weizenfeld correctly" in {
      weizenfeld.price shouldBe 1
      weizenfeld.cardType shouldBe Type.Farm
      weizenfeld.color shouldBe Color.Blue
      weizenfeld.roleNumbers should contain (1)
    }

    "define cafe correctly" in {
      cafe.price shouldBe 2
      cafe.cardType shouldBe Type.Restaurants
      cafe.color shouldBe Color.Red
    }

    "define stadion correctly" in {
      stadion.cardType shouldBe Type.Major_Establishment
      stadion.color shouldBe Color.Purple
    }

    "define funkturm as landmark" in {
      funkturm.cardType shouldBe Type.Landmark
      funkturm.price shouldBe 22
    }

    "execute weizenfeld effect" in {
      val state = stateWithPlayers()

      val result =
        weizenfeld.effect(state, 0)

      result.Players.head.money shouldBe 11
    }

    "execute bauernhof effect" in {
      val state = stateWithPlayers()

      val result =
        bauernhof.effect(state, 0)

      result.Players.head.money shouldBe 11
    }

    "execute baeckerei effect" in {
      val state = stateWithPlayers()

      val result =
        baeckerei.effect(state, 0)

      result.Players.head.money shouldBe 11
    }

    "execute minimarkt effect" in {
      val state = stateWithPlayers()

      val result =
        minimarkt.effect(state, 0)

      result.Players.head.money shouldBe 13
    }

    "execute wald effect" in {
      val state = stateWithPlayers()

      val result =
        wald.effect(state, 0)

      result.Players.head.money shouldBe 11
    }

    "execute bergwerk effect" in {
      val state = stateWithPlayers()

      val result =
        bergwerk.effect(state, 0)

      result.Players.head.money shouldBe 15
    }

    "execute apfelgarten effect" in {
      val state = stateWithPlayers()

      val result =
        apfelgarten.effect(state, 0)

      result.Players.head.money shouldBe 13
    }

    "execute stadion effect" in {
      val state = stateWithPlayers()

      val result =
        stadion.effect(state, 0)

      result.Players.find(_.playerId == 0).get.money shouldBe 12
      result.Players.find(_.playerId == 1).get.money shouldBe 8
    }

    "execute cafe effect" in {
      val state = stateWithPlayers()

      val result =
        cafe.effect(state, 1)

      result.Players.find(_.playerId == 0).get.money shouldBe 9
      result.Players.find(_.playerId == 1).get.money shouldBe 11
    }

    "execute familien restaurant effect" in {
      val state = stateWithPlayers()

      val result =
        familienRestaurant.effect(state, 1)

      result.Players.find(_.playerId == 0).get.money shouldBe 8
      result.Players.find(_.playerId == 1).get.money shouldBe 12
    }

    "execute molkerei effect" in {

      val dairyPlayer = Player(
        money = 0,
        playerId = 0,
        properties = List(
          bauernhof.copy(cardOwnerId = 0),
          bauernhof.copy(cardOwnerId = 0)
        )
      )

      val state =
        Gamestate(Players = List(dairyPlayer))

      val result =
        molkerei.effect(state, 0)

      result.Players.head.money shouldBe 6
    }

    "execute möbelfabrik effect" in {

      val player = Player(
        money = 0,
        playerId = 0,
        properties = List(
          wald.copy(cardOwnerId = 0),
          bergwerk.copy(cardOwnerId = 0)
        )
      )

      val state =
        Gamestate(Players = List(player))

      val result =
        möbelfabrik.effect(state, 0)

      result.Players.head.money shouldBe 6
    }

    "execute markthalle effect" in {

      val player = Player(
        money = 0,
        playerId = 0,
        properties = List(
          weizenfeld.copy(cardOwnerId = 0),
          apfelgarten.copy(cardOwnerId = 0)
        )
      )

      val state =
        Gamestate(Players = List(player))

      val result =
        markthalle.effect(state, 0)

      result.Players.head.money shouldBe 4
    }

    "leave state unchanged for bürohaus" in {
      val state = stateWithPlayers()

      buerohaus.effect(state, 0) shouldBe state
    }

    "leave state unchanged for funkturm" in {
      val state = stateWithPlayers()

      funkturm.effect(state, 0) shouldBe state
    }

    "leave state unchanged for freizeitpark" in {
      val state = stateWithPlayers()

      freizeitpark.effect(state, 0) shouldBe state
    }

    "leave state unchanged for bahnhof" in {
      val state = stateWithPlayers()

      bahnhof.effect(state, 0) shouldBe state
    }

    "leave state unchanged for einkaufszentrum" in {
      val state = stateWithPlayers()

      einkaufszentrum.effect(state, 0) shouldBe state
    }

    "execute fernsehsender effect" in {
      val state = stateWithPlayers()

      val result =
        fernsehsender.effect(state, 0)

      result.Players.head.money shouldBe 11
    }
  }
}