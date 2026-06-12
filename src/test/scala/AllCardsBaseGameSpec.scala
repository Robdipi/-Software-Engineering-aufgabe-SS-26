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
      starterweizenfeld.cardType shouldBe Farm
      starterweizenfeld.color shouldBe Color.Blue
      starterweizenfeld.roleNumbers should contain(1)

      starterbaeckerei.price shouldBe 0
      starterbaeckerei.cardName shouldBe "Bäckerei"
      starterbaeckerei.cardType shouldBe Store
      starterbaeckerei.color shouldBe Color.Green
      starterbaeckerei.roleNumbers should contain allOf (2, 3)
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

    "define every remaining regular card metadata" in {
      bauernhof.cardName shouldBe "Bauernhof"
      bauernhof.price shouldBe 1
      bauernhof.cardType shouldBe Dairy
      bauernhof.roleNumbers should contain(2)

      baeckerei.cardName shouldBe "Bäckerei"
      baeckerei.price shouldBe 1
      baeckerei.cardType shouldBe Store
      baeckerei.roleNumbers should contain allOf (2, 3)

      minimarkt.cardName shouldBe "Mini-Markt"
      minimarkt.price shouldBe 2
      minimarkt.cardType shouldBe Store
      minimarkt.roleNumbers should contain(4)

      wald.cardName shouldBe "wald"
      wald.price shouldBe 3
      wald.cardType shouldBe Industry
      wald.roleNumbers should contain(5)

      buerohaus.cardName shouldBe "Bürohaus"
      buerohaus.price shouldBe 6
      buerohaus.cardType shouldBe Major_Establishment
      buerohaus.roleNumbers should contain(6)

      fernsehsender.cardName shouldBe "Fernsehsender"
      fernsehsender.price shouldBe 7
      fernsehsender.cardType shouldBe Major_Establishment
      fernsehsender.roleNumbers should contain(6)

      molkerei.cardName shouldBe "Molkerei"
      molkerei.price shouldBe 7
      molkerei.cardType shouldBe Secondary_Industry
      molkerei.roleNumbers should contain(7)

      möbelfabrik.cardName shouldBe "Möbelfabrik"
      möbelfabrik.price shouldBe 3
      möbelfabrik.cardType shouldBe Secondary_Industry
      möbelfabrik.roleNumbers should contain(8)

      bergwerk.cardName shouldBe "Bergwerk"
      bergwerk.price shouldBe 6
      bergwerk.cardType shouldBe Industry
      bergwerk.roleNumbers should contain(9)

      familienRestaurant.cardName shouldBe "Familien-Restaurant"
      familienRestaurant.price shouldBe 3
      familienRestaurant.cardType shouldBe Restaurants
      familienRestaurant.roleNumbers should contain allOf (9, 10)

      apfelgarten.cardName shouldBe "apfelgarten"
      apfelgarten.price shouldBe 3
      apfelgarten.cardType shouldBe Farm
      apfelgarten.roleNumbers should contain(10)

      markthalle.cardName shouldBe "Markthalle"
      markthalle.price shouldBe 2
      markthalle.cardType shouldBe Secondary_Industry
      markthalle.roleNumbers should contain allOf (11, 12)

      freizeitpark.cardName shouldBe "Freizeitpark"
      freizeitpark.price shouldBe 16
      freizeitpark.cardType shouldBe Landmark
      freizeitpark.roleNumbers shouldBe empty

      bahnhof.cardName shouldBe "Bahnhof"
      bahnhof.price shouldBe 4
      bahnhof.cardType shouldBe Landmark
      bahnhof.roleNumbers shouldBe empty

      einkaufszentrum.cardName shouldBe "Einkaufszentrum"
      einkaufszentrum.price shouldBe 10
      einkaufszentrum.cardType shouldBe Landmark
      einkaufszentrum.roleNumbers shouldBe empty
    }

    "execute starter card effects" in {
      val state = stateWithPlayers()

      starterweizenfeld.effect(state, 0).Players.find(_.playerId == 0).get.money shouldBe 11
      starterbaeckerei.effect(state, 0).Players.find(_.playerId == 0).get.money shouldBe 11
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
