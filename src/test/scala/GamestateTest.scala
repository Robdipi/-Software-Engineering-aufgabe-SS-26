import de.htwg.se.machikoro.remake.{Gamestate, Player, Type}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.machikoro.remake.allCardsBaseGame.*
import de.htwg.se.machikoro.remake.Gamestate.*
import de.htwg.se.machikoro.remake.Turnstate.*
import de.htwg.se.machikoro.remake.Type.{Restaurants, Store}
import org.scalatest.OptionValues.convertOptionToValuable

class GamestateTest extends AnyWordSpec with Matchers {

  "Gamestate" should {
    "have a working Method changeMoneyOfPlayer" in {
      val start = new Gamestate(Players = List(new Player(playerId = 0),new Player(playerId = 1)))
      start.Players.find(_.playerId == 0).value.money should be(0)
      val end = start.changeMoneyOfPlayer(0, 1)
      end.Players.find(_.playerId == 0).value.money should be(1)
    }
    "have a working Method changeMoneyOfPlayer with extra money" in {
      val state1 = new Gamestate(Players = List(new Player(playerId = 0),new Player(playerId = 1)))
      state1.Players.find(_.playerId == 0).value.money should be(0)
      val state2 = state1.giveCard(0, einkaufszentrum)
      val state3 = state2.changeMoneyOfPlayer(0, 1, Store)
      state3.Players.find(_.playerId == 0).value.money should be(2)
    }
    "have a working Method changeMoneyOfPlayerScaleByType" in {
      val gamestate = new Gamestate(Players = List(new Player(playerId = 0),new Player(playerId = 1)))
      gamestate.Players.find(_.playerId == 0).value.money should be(0)
      val gamestate2 = gamestate.giveCard(0, bauernhof)
      val gamestate3 = gamestate2.giveCard(0, bauernhof)
      val gamestate4 = gamestate3.giveCard(0, bauernhof)
      val gamestate5  = gamestate4.changeMoneyOfPlayerScaleByType(0, Type.Dairy, 2) // 2X3
      gamestate5.Players.find(_.playerId == 0).value.money should be(6)
    }
    "have a working Method transferMoneyBetweenPlayers" in {
      val start = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      start.Players.find(_.playerId == 1).value.money should be(0)
      start.Players.find(_.playerId == 0).value.money should be(0)
      val end = start.transferMoneyBetweenPlayers(0, 1, 1)
      end.Players.find(_.playerId == 1).value.money should be(1)
      end.Players.find(_.playerId == 0).value.money should be(-1)
    }
    "have a working exeption for same values" in {
      val state1 = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      val state2 = state1.transferMoneyBetweenPlayers(0,0,1)
      state1 == state2 should be(true)
    }
    "have a working Method transferMoneyBetweenPlayers with extra Money" in {
      val state1 = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state1.Players.find(_.playerId == 1).value.money should be(0)
      state1.Players.find(_.playerId == 0).value.money should be(0)
      state1.Players.find(_.playerId == 2).value.money should be(0)

      val state2 = state1.giveCard(1, einkaufszentrum)
      val state3 = state2.transferMoneyBetweenPlayers(0, 1, 1, Restaurants)
      state3.Players.find(_.playerId == 1).value.money should be(2)
      state3.Players.find(_.playerId == 0).value.money should be(-2)
      state1.Players.find(_.playerId == 2).value.money should be(0)
    }
    "have a working Method stealFromEveryone" in {
      val start = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      val end = start.stealFromEveryone(1, 1)
      end.Players.find(_.playerId == 1).value.money should be(2)
      end.Players.find(_.playerId == 0).value.money should be(-1)
      end.Players.find(_.playerId == 2).value.money should be(-1)
    }
    "have a working Method activateCards" in {
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1),new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)
      state = state.giveCard(0, bauernhof)
      state = state.giveCard(0, baeckerei)

      state = state.giveCard(1, bauernhof)
      state = state.giveCard(1, baeckerei)

      state = state.giveCard(2, bauernhof)
      state = state.giveCard(2, baeckerei)

      state = state.activateCards(2,0)

      state.Players.find(_.playerId == 0).value.money should be(2)
      state.Players.find(_.playerId == 1).value.money should be(1)
      state.Players.find(_.playerId == 2).value.money should be(1)
    }
    "have a working Method iterateTurn" in {
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1, GetsAnotherTurn = true), new Player(playerId = 2)))
      state = state.iterateTurn()
      state.curentTurn should be (1)
      state.CurrentTurnPlayerId should be(1)

      state = state.iterateTurn()
      state.curentTurn should be(2)
      state.CurrentTurnPlayerId should be(1)

      state = state.iterateTurn()
      state.curentTurn should be(3)
      state.CurrentTurnPlayerId should be(2)
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
