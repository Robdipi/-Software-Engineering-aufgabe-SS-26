import de.htwg.se.machikoro.remake.model.*

class cardSpec {


  import org.scalatest.wordspec.AnyWordSpec
  import org.scalatest.matchers.should.Matchers

  class CardSpec extends AnyWordSpec with Matchers {

    "A card" should {

      "execute its effect when activate is called" in {
        val cardEffect = card(
          cardName = "Test",
          cardOwnerId = 0,
          effect = (g, owner) => g.changeMoneyOfPlayer(owner, 5)
        )

        val player = Player(money = 10, playerId = 0)
        val game = Gamestate(Players = List(player))

        val result = cardEffect.activate(game)

        result.Players.head.money shouldBe 15
      }

      "return a readable string" in {
        val c = card(
          cardName = "Cafe",
          price = 2,
          cardOwnerId = 0
        )

        c.cardToString() should include("Cafe")
        c.cardToString() should include("2")
      }
    }
  }
}
