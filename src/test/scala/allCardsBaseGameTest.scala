import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.machikoro.remake.{Gamestate, Player, Type}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.machikoro.remake.allCardsBaseGame.*
import de.htwg.se.machikoro.remake.Gamestate.*
import de.htwg.se.machikoro.remake.Type.{Restaurants, Store}
import org.scalatest.OptionValues.convertOptionToValuable

class allCardsBaseGameTest extends AnyWordSpec with Matchers {
  "All cards not testet anywhere else" should {
    "Weizenfeld" in {
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)
      state = state.giveCard(0, weizenfeld)
      state = state.activateCards(1,1)
      state.Players.find(_.playerId == 0).value.money should be(1)
      state = state.giveCard(0, weizenfeld)
      state = state.activateCards(1, 2)
      state = state.activateCards(2, 1)
      state.Players.find(_.playerId == 0).value.money should be(3)
    }
    "apfelgarten" in {
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)
      state = state.giveCard(0, apfelgarten)
      state = state.activateCards(10, 1)
      state.Players.find(_.playerId == 0).value.money should be(3)
      state = state.giveCard(0, apfelgarten)
      state = state.activateCards(10, 2)
      state = state.activateCards(2, 1)
      state.Players.find(_.playerId == 0).value.money should be(9)
    }
    "starterweizenfeld" in{
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)
      state = state.giveCard(0, starterweizenfeld)
      state = state.activateCards(1,0)
      state.Players.find(_.playerId == 0).value.money should be(1)
      state = state.giveCard(0, starterweizenfeld)
      state = state.activateCards(1, 2)
      state = state.activateCards(2, 1)
      state.Players.find(_.playerId == 0).value.money should be(3)}
    "starterbaeckerei" in{
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)
      state = state.giveCard(0, starterbaeckerei)
      state = state.activateCards(2, 0)
      state.Players.find(_.playerId == 0).value.money should be(1)
      state = state.giveCard(0, starterbaeckerei)
      state = state.activateCards(3, 0)
      state = state.activateCards(3, 1)
      state.Players.find(_.playerId == 0).value.money should be(3)
    }
    "cafe" in{
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)
      state = state.giveCard(0, cafe)
      state = state.iterateTurn()  //default its 0 so
      state = state.activateCards(3, 1)
      state.Players.find(_.playerId == 0).value.money should be(1)
      state.Players.find(_.playerId == 1).value.money should be(-1)
      state = state.giveCard(0, einkaufszentrum) // to test if the combo works

      state = state.iterateTurn()
      state = state.activateCards(3, 2)
      state.Players.find(_.playerId == 0).value.money should be(3)
      state.Players.find(_.playerId == 1).value.money should be(-1)
      state.Players.find(_.playerId == 2).value.money should be(-2)

      state = state.iterateTurn()
      state = state.activateCards(3, 0) // steal from yourself nothing should change
      state.Players.find(_.playerId == 0).value.money should be(3)
      state.Players.find(_.playerId == 1).value.money should be(-1)
      state.Players.find(_.playerId == 2).value.money should be(-2)

    }
    "minimarkt" in{
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)
      state = state.giveCard(0, minimarkt)
      state = state.activateCards(4, 0)
      state.Players.find(_.playerId == 0).value.money should be(3)
      state = state.giveCard(0, minimarkt)
      state = state.activateCards(4, 0)
      state = state.activateCards(3, 1)
      state.Players.find(_.playerId == 0).value.money should be(9)
    }
    "wald" in{
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)
      state = state.giveCard(0, wald)
      state = state.activateCards(5, 0)
      state.Players.find(_.playerId == 0).value.money should be(1)
      state = state.giveCard(0, wald)
      state = state.activateCards(5, 2)
      state = state.activateCards(2, 1)
      state.Players.find(_.playerId == 0).value.money should be(3)
    }
    "stadion" in{
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)
      state = state.giveCard(0, stadion)
      state = state.activateCards(6, 0)
      state.Players.find(_.playerId == 1).value.money should be(-2)
      state.Players.find(_.playerId == 0).value.money should be(4)
      state.Players.find(_.playerId == 2).value.money should be(-2)
    }
    "fernsehsender" in{
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)
      state = state.giveCard(0, fernsehsender)
      state = state.activateCards(6, 0)
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(1)//Todo change this
      state.Players.find(_.playerId == 2).value.money should be(0)
    }
    "molkerei" in{
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)

      state = state.giveCard(0, molkerei)
      state = state.activateCards(7, 0)
      state.Players.find(_.playerId == 0).value.money should be(0)

      state = state.giveCard(0, bauernhof)
      state = state.activateCards(7, 0)
      state.Players.find(_.playerId == 0).value.money should be(3)

      state = state.giveCard(0, bauernhof)
      state = state.activateCards(7, 0)
      state.Players.find(_.playerId == 0).value.money should be(9)

      state = state.giveCard(0, molkerei)
      state = state.activateCards(7, 0)
      state.Players.find(_.playerId == 0).value.money should be(21)

      state = state.giveCard(0, molkerei)
      state = state.activateCards(7, 0)
      state = state.activateCards(3, 1)
      state = state.activateCards(7, 1)

      state.Players.find(_.playerId == 0).value.money should be(39)
    }
    "möbelfabrik" in{
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)

      state = state.giveCard(0, möbelfabrik)
      state = state.activateCards(8, 0)
      state.Players.find(_.playerId == 0).value.money should be(0)

      state = state.giveCard(0, wald)
      state = state.activateCards(8, 0)
      state.Players.find(_.playerId == 0).value.money should be(3)

      state = state.giveCard(0, bergwerk)
      state = state.activateCards(8, 0)
      state.Players.find(_.playerId == 0).value.money should be(9)

      state = state.giveCard(0, möbelfabrik)
      state = state.activateCards(8, 0)
      state.Players.find(_.playerId == 0).value.money should be(21)

      state = state.giveCard(0, möbelfabrik)
      state = state.activateCards(8, 0)
      state = state.activateCards(3, 1)
      state = state.activateCards(7, 1)

      state.Players.find(_.playerId == 0).value.money should be(39)
    }
    "bergwerk" in {
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)
      state = state.giveCard(0, bergwerk)
      state = state.activateCards(9, 0)
      state.Players.find(_.playerId == 0).value.money should be(5)
      state = state.giveCard(0, bergwerk)
      state = state.activateCards(9, 2)
      state = state.activateCards(2, 1)
      state.Players.find(_.playerId == 0).value.money should be(15)
    }
    "familienRestaurant" in {
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)
      state = state.giveCard(0, familienRestaurant)
      state = state.iterateTurn() //default its 0 so
      state = state.activateCards(9, 1)
      state.Players.find(_.playerId == 0).value.money should be(2)
      state.Players.find(_.playerId == 1).value.money should be(-2)
      state = state.giveCard(0, einkaufszentrum) // to test if the combo works

      state = state.iterateTurn()
      state = state.activateCards(10, 2)
      state.Players.find(_.playerId == 0).value.money should be(5)
      state.Players.find(_.playerId == 1).value.money should be(-2)
      state.Players.find(_.playerId == 2).value.money should be(-3)

      state = state.iterateTurn()
      state = state.activateCards(9, 0) // steal from yourself nothing should change
      state.Players.find(_.playerId == 0).value.money should be(5)
      state.Players.find(_.playerId == 1).value.money should be(-2)
      state.Players.find(_.playerId == 2).value.money should be(-3)
      print(state.Players.find(_.playerId == 0).value.printAllCards())
    }
    "markthalle" in {
      var state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1), new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)

      state = state.giveCard(0, markthalle)
      state = state.activateCards(11, 0)
      state.Players.find(_.playerId == 0).value.money should be(0)

      state = state.giveCard(0, weizenfeld)
      state = state.activateCards(12, 0)
      state.Players.find(_.playerId == 0).value.money should be(2)

      state = state.giveCard(0, apfelgarten)
      state = state.activateCards(12, 0)
      state.Players.find(_.playerId == 0).value.money should be(6)

      state = state.giveCard(0, markthalle)
      state = state.activateCards(11, 0)
      state.Players.find(_.playerId == 0).value.money should be(14)

      state = state.giveCard(0, markthalle)
      state = state.activateCards(11, 0)
      state = state.activateCards(3, 1)
      state = state.activateCards(12, 1)

      state.Players.find(_.playerId == 0).value.money should be(26)
    }

  }

}
