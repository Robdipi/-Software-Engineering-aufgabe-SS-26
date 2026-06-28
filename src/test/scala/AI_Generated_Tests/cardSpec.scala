package AI_Generated_Tests

import de.htwg.se.machikoro.remake.model.*
import de.htwg.se.machikoro.remake.model.Data.{Card, Gamestate, Player}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardSpec extends AnyWordSpec with Matchers {

  "A Card" should {

    "execute its effect when activate is called" in {
      val card = Card(
        cardName = "Test",
        cardOwnerId = 0,
        effect = (g, owner) => g.changeMoneyOfPlayer(owner, 5)
      )

      val game = Gamestate(Players = List(Player(money = 10, playerId = 0)))

      card.activate(game).Players.head.money shouldBe 15
    }

    "return unchanged gamestate for the default effect" in {
      val card = Card(cardOwnerId = 0)
      val game = Gamestate(Players = List(Player(money = 10, playerId = 0)))

      card.activate(game) shouldBe game
    }

    "return a readable string" in {
      val card = Card(
        cardName = "Cafe",
        price = 2,
        description = "Erhalte Geld.",
        cardOwnerId = 0
      )

      card.cardToString() shouldBe "|Cafe|costs: 2|Erhalte Geld.|"
    }
  }
}
