import de.htwg.se.machikoro.remake.{Gamestate, Player, card}
import de.htwg.se.machikoro.remake.allCardsBaseGame.*
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardAdditionalTest extends AnyWordSpec with Matchers {

  "card" should {
    "activate its effect with the stored owner id" in {
      val state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1)))
      val ownedBergwerk = bergwerk.copy(cardOwnerId = 1)

      val changed = ownedBergwerk.activate(state)

      changed.Players.find(_.playerId == 0).value.money should be(0)
      changed.Players.find(_.playerId == 1).value.money should be(5)
    }

    "default activate should not change the gamestate" in {
      val state = new Gamestate(Players = List(new Player(playerId = 0)))
      val defaultCard = new card(cardOwnerId = 0)

      defaultCard.activate(state) should be(state)
    }

    "cardToString should use the card fields" in {
      val customCard = new card(
        cardName = "Testkarte",
        price = 7,
        description = "Beschreibung",
        cardOwnerId = 0
      )

      customCard.cardToString() should be("|Testkarte|costs: 7|Beschreibung|")
    }
  }
}
