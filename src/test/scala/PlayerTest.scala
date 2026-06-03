import Model.Player
import de.htwg.se.machikoro.remake.Gamestate
import Model.allCardsBaseGame.*
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.OptionValues.convertOptionToValuable
import Model.allCardsBaseGame.*


class PlayerTest extends AnyWordSpec with Matchers {

  "Player" should {
    "have a working Method canChooseDyeAmount" in {
      val start = new Gamestate(Players = List(new Player(playerId = 0)))
      start.Players.find(_.playerId == 0).exists(_.canChooseDyeAmount()) should be (false)
      val end = start.giveCard(0, bahnhof.copy(cardOwnerId = 0))
      end.Players.find(_.playerId == 0).exists(_.canChooseDyeAmount()) should be (true)
    }
    "have a working Method canGetAnotherTurn" in {
      val start = new Gamestate(Players = List(new Player(playerId = 0)))
      start.Players.find(_.playerId == 0).exists(_.canGetAnotherTurn()) should be(false)
      val end = start.giveCard(0, freizeitpark.copy(cardOwnerId = 0))
      end.Players.find(_.playerId == 0).exists(_.canGetAnotherTurn()) should be(true)
    }
    "have a working Method canRejectDyeTrow" in {
      val start = new Gamestate(Players = List(new Player(playerId = 0)))
      start.Players.find(_.playerId == 0).exists(_.canRejectDyeTrow()) should be(false)
      val end = start.giveCard(0, funkturm.copy(cardOwnerId = 0))
      end.Players.find(_.playerId == 0).exists(_.canRejectDyeTrow()) should be(true)
    }
    "have a working Method getExtraMoney" in {
      val start = new Gamestate(Players = List(new Player(playerId = 0)))
      start.Players.find(_.playerId == 0).exists(_.getExtraMoney()) should be(false)
      val end = start.giveCard(0, einkaufszentrum.copy(cardOwnerId = 0))
      end.Players.find(_.playerId == 0).exists(_.getExtraMoney()) should be(true)
    }
    
  
  }


}