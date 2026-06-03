import Controller.{InputManager, RandomnessManager}
import Model.Player
import de.htwg.se.machikoro.remake.{Gamestate, Type}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import Model.allCardsBaseGame.*
import de.htwg.se.machikoro.remake.Gamestate.*
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
      val state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1),new Player(playerId = 2)))
      state.Players.find(_.playerId == 1).value.money should be(0)
      state.Players.find(_.playerId == 0).value.money should be(0)
      state.Players.find(_.playerId == 2).value.money should be(0)
      val state2 = state.giveCard(0, bauernhof).giveCard(0, baeckerei).giveCard(1, bauernhof).giveCard(1, baeckerei)
        .giveCard(2, bauernhof).giveCard(2, baeckerei).activateCards(2,0)

      state2.Players.find(_.playerId == 0).value.money should be(2)
      state2.Players.find(_.playerId == 1).value.money should be(1)
      state2.Players.find(_.playerId == 2).value.money should be(1)
    }
    "have a working Method iterateTurn" in {
      val state0 = new Gamestate(
        Players = List(
          new Player(playerId = 0),
          new Player(playerId = 1, GetsAnotherTurn = true),
          new Player(playerId = 2)
        )
      )

      val state1 = state0.iterateTurn()
      state1.curentTurn should be(1)
      state1.CurrentTurnPlayerId should be(1)

      val state2 = state1.iterateTurn()
      state2.curentTurn should be(2)
      state2.CurrentTurnPlayerId should be(1)

      val state3 = state2.iterateTurn()
      state3.curentTurn should be(3)
      state3.CurrentTurnPlayerId should be(2)
    }
    "have a working Method choseDiceamount and getDiceAmount" should {

      "work for single dice without extra turn" in {
        val gameState0 = new Gamestate().initializeStandartGame(4)
        
        val gameState1 = gameState0.copy(rndManager = new RandomnessManager(numbers = List(1))).choseDiceamount()

        gameState1.DiceResult should be(1)
        gameState1.diceChoosen should be(1)
        gameState1.Players
          .find(_.playerId == gameState1.CurrentTurnPlayerId)
          .value.GetsAnotherTurn should be(false)
      }

      "work for two dice with bad input corrected" in {
        val gameState0 = new Gamestate().initializeStandartGame(4)

        val gameState1 = gameState0.iterateTurn().giveCard(1, bahnhof)
          .copy(rndManager = new RandomnessManager(numbers = List(3,5)),inputManager =  new InputManager(inputs = List("dfghjklölkjhgfdfghjk1","2")))//bad input  and real one 2
        

       

        val gameState2 = gameState1.choseDiceamount()

        gameState2.DiceResult should be(8)
        gameState2.diceChoosen should be(2)
        gameState2.Players
          .find(_.playerId == gameState2.CurrentTurnPlayerId)
          .value.GetsAnotherTurn should be(false)
      }

      "work for single dice after choosing explicitly" in {
        val gameState0 = new Gamestate().initializeStandartGame(4)

        val gameState1 = gameState0.iterateTurn().giveCard(2, bahnhof).copy(rndManager = new RandomnessManager(numbers = List(1,3)),inputManager =  new InputManager(inputs = List("dfghjklölkjhgfdfghjk1","1")))
        

        val gameState2 = gameState1.choseDiceamount()

        gameState2.DiceResult should be(1)
        gameState2.diceChoosen should be(1)
        gameState2.Players
          .find(_.playerId == gameState2.CurrentTurnPlayerId)
          .value.GetsAnotherTurn should be(false)
      }

      "grant extra turn when rolling doubles with correct cards" in {
        val gameState0 = new Gamestate().initializeStandartGame(4)

        val gameState1 = gameState0
          .iterateTurn()
          .giveCard(1, bahnhof)
          .giveCard(1, freizeitpark)
          .copy(rndManager = new RandomnessManager(numbers = List(3,3)), inputManager = new InputManager(inputs = List("2")))

        

        val gameState2 = gameState1.choseDiceamount()

        gameState2.DiceResult should be(6)
        gameState2.diceChoosen should be(2)
        gameState2.Players
          .find(_.playerId == gameState2.CurrentTurnPlayerId)
          .value.GetsAnotherTurn should be(true)
      }
    }
  }
}
   