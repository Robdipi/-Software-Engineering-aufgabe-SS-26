package AI_Generated_Tests

import de.htwg.se.machikoro.remake.model.*
import de.htwg.se.machikoro.remake.model.Data.AllCardsBaseGame.*
import de.htwg.se.machikoro.remake.model.Data.{Color, Gamestate, Player, Type}
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

  private def moneyOf(state: Gamestate, playerId: Int): Int =
    state.Players.find(_.playerId == playerId).get.money

  "allCardsBaseGame" should {

    "define important cards correctly" in {
      weizenfeld.price shouldBe 1
      weizenfeld.cardType shouldBe Type.Farm
      weizenfeld.color shouldBe Color.Blue
      weizenfeld.roleNumbers should contain(1)

      cafe.price shouldBe 2
      cafe.cardType shouldBe Type.Restaurants
      cafe.color shouldBe Color.Red

      stadion.cardType shouldBe Type.Major_Establishment
      stadion.color shouldBe Color.Purple

      funkturm.cardType shouldBe Type.Landmark
      funkturm.color shouldBe Color.Yellow
      funkturm.price shouldBe 22
    }

    "execute bank money effects" in {
      val state = stateWithPlayers()

      weizenfeld.effect(state, 0).Players.head.money shouldBe 11
      starterweizenfeld.effect(state, 0).Players.head.money shouldBe 11
      bauernhof.effect(state, 0).Players.head.money shouldBe 11
      baeckerei.effect(state, 0).Players.head.money shouldBe 11
      starterbaeckerei.effect(state, 0).Players.head.money shouldBe 11
      minimarkt.effect(state, 0).Players.head.money shouldBe 13
      wald.effect(state, 0).Players.head.money shouldBe 11
      bergwerk.effect(state, 0).Players.head.money shouldBe 15
      apfelgarten.effect(state, 0).Players.head.money shouldBe 13
      fernsehsender.effect(state, 0).Players.head.money shouldBe 11
    }

    "execute cafe effect" in {
      val result = cafe.effect(stateWithPlayers(), 1)

      moneyOf(result, 0) shouldBe 9
      moneyOf(result, 1) shouldBe 11
    }

    "execute familienRestaurant effect" in {
      val result = familienRestaurant.effect(stateWithPlayers(), 1)

      moneyOf(result, 0) shouldBe 8
      moneyOf(result, 1) shouldBe 12
    }

    "execute stadion effect" in {
      val result = stadion.effect(stateWithPlayers(), 0)

      moneyOf(result, 0) shouldBe 12
      moneyOf(result, 1) shouldBe 8
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

      molkerei.effect(Gamestate(Players = List(player)), 0).Players.head.money shouldBe 6
    }

    "execute moebelfabrik effect scaled by industry cards" in {
      val player = Player(
        money = 0,
        playerId = 0,
        properties = List(
          wald.copy(cardOwnerId = 0),
          bergwerk.copy(cardOwnerId = 0)
        )
      )

      möbelfabrik.effect(Gamestate(Players = List(player)), 0).Players.head.money shouldBe 6
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

      markthalle.effect(Gamestate(Players = List(player)), 0).Players.head.money shouldBe 4
    }

    "leave state unchanged for passive effects" in {
      val state = stateWithPlayers()

      buerohaus.effect(state, 0) shouldBe state
      funkturm.effect(state, 0) shouldBe state
      freizeitpark.effect(state, 0) shouldBe state
      bahnhof.effect(state, 0) shouldBe state
      einkaufszentrum.effect(state, 0) shouldBe state
    }

    "execute fernsehsender effect" in {
      val state = stateWithPlayers()
      val result = fernsehsender.effect(state, 0)
      moneyOf(result, 0) shouldBe 11
    }

    "execute buerohaus effect returns unchanged state" in {
      val state = stateWithPlayers()
      buerohaus.effect(state, 0) shouldBe state
    }

    "handle scaled effects when player has no matching cards" in {
      val player = Player(money = 0, playerId = 0, properties = List())
      val state = Gamestate(Players = List(player))

      molkerei.effect(state, 0).Players.head.money shouldBe 0
      möbelfabrik.effect(state, 0).Players.head.money shouldBe 0
      markthalle.effect(state, 0).Players.head.money shouldBe 0
    }

    "verify card definitions have expected prices" in {
      weizenfeld.price shouldBe 1
      starterweizenfeld.price shouldBe 0
      bauernhof.price shouldBe 1
      baeckerei.price shouldBe 1
      starterbaeckerei.price shouldBe 0
      cafe.price shouldBe 2
      minimarkt.price shouldBe 2
      wald.price shouldBe 3
      buerohaus.price shouldBe 6
      stadion.price shouldBe 6
      fernsehsender.price shouldBe 7
      molkerei.price shouldBe 7
      möbelfabrik.price shouldBe 3
      bergwerk.price shouldBe 6
      familienRestaurant.price shouldBe 3
      apfelgarten.price shouldBe 3
      markthalle.price shouldBe 2
      funkturm.price shouldBe 22
      freizeitpark.price shouldBe 16
      bahnhof.price shouldBe 4
      einkaufszentrum.price shouldBe 10
    }

    "verify card definitions have expected names" in {
      weizenfeld.cardName shouldBe "Weizenfeld"
      bauernhof.cardName shouldBe "Bauernhof"
      baeckerei.cardName shouldBe "Bäckerei"
      cafe.cardName shouldBe "Cafe"
      minimarkt.cardName shouldBe "Mini-Markt"
      wald.cardName shouldBe "wald"
      buerohaus.cardName shouldBe "Bürohaus"
      stadion.cardName shouldBe "stadion"
      fernsehsender.cardName shouldBe "Fernsehsender"
      molkerei.cardName shouldBe "Molkerei"
      möbelfabrik.cardName shouldBe "Möbelfabrik"
      bergwerk.cardName shouldBe "Bergwerk"
      familienRestaurant.cardName shouldBe "Familien-Restaurant"
      apfelgarten.cardName shouldBe "apfelgarten"
      markthalle.cardName shouldBe "Markthalle"
      funkturm.cardName shouldBe "Funkturm"
      freizeitpark.cardName shouldBe "Freizeitpark"
      bahnhof.cardName shouldBe "Bahnhof"
      einkaufszentrum.cardName shouldBe "Einkaufszentrum"
    }
  }
}
