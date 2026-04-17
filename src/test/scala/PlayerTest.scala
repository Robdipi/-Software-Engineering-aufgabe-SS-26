import de.htwg.se.machikoro.remake.{Gamestate, Player}
import de.htwg.se.machikoro.remake.allCardsBaseGame.*
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.OptionValues.convertOptionToValuable

class PlayerTest extends AnyWordSpec with Matchers {

  "Player" should {
    "have a working Method canChooseDyeAmount" in {
      val start = new Gamestate(Players = List(new Player(playerId = 0)))
      start.Players.find(_.playerId == 1).value.canChooseDyeAmount() should be(false)
      
    }
    "have a working Method canGetAnotherTurn" in {
    }
    "have a working Method canRejectDyeTrow" in {
    }
    "have a working Method getExtraMoney" in {
    }
  }
  
}