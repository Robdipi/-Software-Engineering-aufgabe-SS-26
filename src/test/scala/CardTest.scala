import de.htwg.se.machikoro.remake.allCardsBaseGame.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class CardTest extends AnyWordSpec with Matchers {
  "cardToString" should {
    "return the expected string" in {
      val c1 = weizenfeld.copy(cardOwnerID = 0)
      c1.cardToString() shouldEqual "|Weizenfeld|costs: 1|erhalte 1 Münze aus der Bank.|"
    }
    "have a working method activate" in {

    }
  }
}