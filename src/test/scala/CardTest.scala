import de.htwg.se.machikoro.remake.Type.Restaurants
import de.htwg.se.machikoro.remake.{Gamestate, Player, card}
import de.htwg.se.machikoro.remake.allCardsBaseGame.*
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class CardTest extends AnyWordSpec with Matchers {
  "cardToString" should {
    "return the expected string" in {
      val c1 = weizenfeld.copy(cardOwnerId = 0)
      c1.cardToString() shouldEqual "|Weizenfeld|costs: 1|erhalte 1 Münze aus der Bank.|"
    }
    "have working standart parameters" in {
      val c1 = new card(cardOwnerId = 0)
      c1.cardToString() shouldEqual "|Weizenfeld|costs: 0|erhalte 1 Münze aus der Bank.|"
    }
    "have a working method activate" in {
      /*val state1 = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1)))
      state1.Players.find(_.playerId == 1).value.money should be(0)
      state1.Players.find(_.playerId == 0).value.money should be(0)
      val state2 = state1.giveCard(0, bergwerk)
      val state3 = state2.Players.find(_.playerId == 0).value.properties.find(_.cardName.equals(bergwerk.cardName)).value.activate(state2)
      state1.Players.find(_.playerId == 1).value.money should be(0)
      state1.Players.find(_.playerId == 0).value.money should be(3)*/
    }
  }
}