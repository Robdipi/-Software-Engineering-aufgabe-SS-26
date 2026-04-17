import de.htwg.se.machikoro.remake.{Gamestate, Player}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.machikoro.remake.allCardsBaseGame.*
import de.htwg.se.machikoro.remake.Gamestate.*
import de.htwg.se.machikoro.remake.Turnstate.*
import de.htwg.se.machikoro.remake.Type

import org.scalatest.OptionValues.convertOptionToValuable

class GamestateTest extends AnyWordSpec with Matchers {

  "Gamestate" should {
    "have a working Method changeMoneyOfPlayer" in {
      val start = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1)))
      val end = start.changeMoneyOfPlayer(1, 1)
      end.Players.find(_.playerId == 1).value.money should be(1)
    }
    "have a working Method changeMoneyOfPlayerScaleByType" in {
      val start = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1, properties = List(bauernhof.copy(cardOwnerID = 1), bauernhof.copy(cardOwnerID = 1), bauernhof.copy(cardOwnerID = 1)))))
      val end = start.changeMoneyOfPlayerScaleByType(1, Type.Dairy, 2) // 2X3
      end.Players.find(_.playerId == 1).value.money should be(6)
    }
    "have a working Method transferMoneyBetweenPlayers" in {
      val start = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1)))
      val end = start.transferMoneyBetweenPlayers(0, 1, 1)
      end.Players.find(_.playerId == 1).value.money should be(1)
      end.Players.find(_.playerId == 0).value.money should be(-1)
    }
    "have a working Method stealFromEveryone" in {
      val start = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      val end = start.stealFromEveryone(1, 1)
      end.Players.find(_.playerId == 1).value.money should be(2)
      end.Players.find(_.playerId == 0).value.money should be(-1)
      end.Players.find(_.playerId == 2).value.money should be(-1)
    }
  }
}
    /*
    "have a working Method giveCard" in {
      val start = new Gamestate(Players = List(new Player(playerId = 0)))
      start.Players
        .find(_.playerId == 0)
        .exists(_.properties.contains(weizenfeld.copy(cardOwnerID = 0))) should be(false)

      val changed = start.giveCard(0,weizenfeld.copy(cardOwnerID = 0))

      changed.Players
        .find(_.playerId == 0)
        .exists(_.properties.contains(weizenfeld.copy(cardOwnerID = 0))) should be(true)

      changed.Players.find(_.playerId == 0).value.properties.size should be(1)
    }

  }
*/
