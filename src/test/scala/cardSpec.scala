package de.htwg.se.machikoro.remake.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardSpec extends AnyWordSpec with Matchers {

  "A Card" should {

    "execute its effect when activate is called" in {
      val card = Card(
        cardName = "Testkarte",
        cardOwnerId = 0,
        effect = (g, owner) => g.changeMoneyOfPlayer(owner, 5)
      )

      val game = Gamestate(Players = List(Player(money = 10, playerId = 0)))
      val result = card.activate(game)

      result.Players.head.money shouldBe 15
    }

    "keep the state unchanged when the default effect is used" in {
      val card = Card(cardOwnerId = 0)
      val game = Gamestate(Players = List(Player(money = 10, playerId = 0)))

      card.activate(game) shouldBe game
    }

    "return the expected string representation" in {
      val card = Card(
        cardName = "Cafe",
        price = 2,
        description = "Erhalte 1 Münze.",
        cardOwnerId = 0
      )

      card.cardToString() shouldBe "|Cafe|costs: 2|Erhalte 1 Münze.|"
    }
  }
}
