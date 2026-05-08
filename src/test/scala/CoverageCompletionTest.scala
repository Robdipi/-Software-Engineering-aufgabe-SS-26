import de.htwg.se.machikoro.remake.{Gamestate, InputManager, Player, RandomnessManager, cardStack}
import de.htwg.se.machikoro.remake.allCardsBaseGame.*
import de.htwg.se.machikoro.remake.Color
import de.htwg.se.machikoro.remake.main
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import scala.Console

class CoverageCompletionTest extends AnyWordSpec with Matchers {

  private def silence[A](block: => A): A = {
    val out = new ByteArrayOutputStream()
    Console.withOut(out)(block)
  }

  "Coverage completion for remaining branches" should {
    "choose one die explicitly when the current player owns Bahnhof" in silence {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0)),
        CurrentTurnPlayerId = 0,
        inputManager = new InputManager(inputs = List("1")),
        rndManager = new RandomnessManager(numbers = List(5, 6))
      ).giveCard(0, bahnhof)

      val changed = state.choseDiceamount()

      changed.diceChoosen should be(1)
      changed.DiceResult should be(5)
      changed.inputManager.index should be(0)
    }

    "return unchanged from checkingResult when the current player has no Funkturm" in silence {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0)),
        CurrentTurnPlayerId = 0,
        DiceResult = 4,
        diceChoosen = 1,
        rndManager = new RandomnessManager(numbers = List(1, 2))
      )

      state.checkingResult() should be(state)
    }

    "reroll two dice without granting another turn when the dice are not a double" in silence {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0)),
        CurrentTurnPlayerId = 0,
        DiceResult = 7,
        diceChoosen = 2,
        inputManager = new InputManager(inputs = List("n")),
        rndManager = new RandomnessManager(numbers = List(1, 2))
      ).giveCard(0, funkturm)

      val changed = state.checkingResult()

      changed.DiceResult should be(3)
      changed.Players.find(_.playerId == 0).value.GetsAnotherTurn should be(false)
    }

    "ask again for rejection after invalid input" in silence {
      val state = new Gamestate(inputManager = new InputManager(inputs = List("maybe", "n")))

      val (rejected, nextInputManager) = state.askForRejection(state.inputManager)

      rejected should be(true)
      nextInputManager.index should be(0)
    }

    "execute BuyPhase and allow the player to buy nothing" in silence {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0, money = 10)),
        CurrentTurnPlayerId = 0,
        cardStacks = List(cardStack(2, weizenfeld)),
        inputManager = new InputManager(inputs = List("next"))
      )

      val changed = state.BuyPhase()

      changed.Players.find(_.playerId == 0).value.money should be(10)
      changed.cardStacks.find(_.stackCard.cardName == "Weizenfeld").value.amount should be(2)
    }

    "ask again when the player tries to buy a duplicate yellow landmark" in silence {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0, money = 100)),
        CurrentTurnPlayerId = 0,
        cardStacks = List(cardStack(1, bahnhof)),
        inputManager = new InputManager(inputs = List("Bahnhof", "next"))
      ).giveCard(0, bahnhof)

      val changed = state.askForCardToBuy(state.inputManager)

      changed.Players.find(_.playerId == 0).value.properties.count(_.cardName == "Bahnhof") should be(1)
      changed.cardStacks.find(_.stackCard.cardName == "Bahnhof").value.amount should be(1)
    }

    "ask again when the player tries to buy a second purple card" in silence {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0, money = 100)),
        CurrentTurnPlayerId = 0,
        cardStacks = List(cardStack(1, buerohaus)),
        inputManager = new InputManager(inputs = List("Bürohaus", "next"))
      ).giveCard(0, stadion)

      val changed = state.askForCardToBuy(state.inputManager)

      changed.Players.find(_.playerId == 0).value.properties.count(_.color == Color.Purple) should be(1)
      changed.cardStacks.find(_.stackCard.cardName == "Bürohaus").value.amount should be(1)
    }

    "ask again when the player cannot afford the selected card" in silence {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0, money = 0), new Player(playerId = 1, money = 0)),
        CurrentTurnPlayerId = 0,
        cardStacks = List(cardStack(1, weizenfeld)),
        inputManager = new InputManager(inputs = List("Weizenfeld", "next"))
      )

      val changed = state.askForCardToBuy(state.inputManager)

      changed.Players.find(_.playerId == 0).value.money should be(0)
      changed.Players.find(_.playerId == 0).value.properties should be(empty)
      changed.cardStacks.find(_.stackCard.cardName == "Weizenfeld").value.amount should be(1)
    }

    "ask again when the player asks for non existent card" in silence {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0, money = 0), new Player(playerId = 1, money = 0)),
        CurrentTurnPlayerId = 0,
        cardStacks = List(cardStack(1, weizenfeld)),
        inputManager = new InputManager(inputs = List("Kohlekraftwerk", "next"))
      )

      val changed = state.askForCardToBuy(state.inputManager)

      changed.Players.find(_.playerId == 0).value.money should be(0)
      changed.Players.find(_.playerId == 0).value.properties should be(empty)
      changed.cardStacks.find(_.stackCard.cardName == "Weizenfeld").value.amount should be(1)
    }

    "ask again when the selected stack is empty" in silence {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0, money = 10)),
        CurrentTurnPlayerId = 0,
        cardStacks = List(cardStack(0, weizenfeld)),
        inputManager = new InputManager(inputs = List("next"))
      )

      val changed = state.actuallyBuyCard(weizenfeld, cardStack(0, weizenfeld), state.inputManager)

      changed.Players.find(_.playerId == 0).value.money should be(10)
      changed.Players.find(_.playerId == 0).value.properties should be(empty)
      changed.cardStacks.find(_.stackCard.cardName == "Weizenfeld").value.amount should be(0)
    }

    "read from stdin when InputManager has no prepared inputs" in silence {
      val input = new ByteArrayInputStream("hello-from-stdin\n".getBytes("UTF-8"))

      val (value, nextManager) = Console.withIn(input) {
        new InputManager().getNextInput("message")
      }

      value should be("hello-from-stdin")
      nextManager should be(new InputManager())
    }

    "generate a random die value when RandomnessManager has no prepared numbers" in {
      val (value, nextManager) = new RandomnessManager().getNextNum

      value should be >= 1
      value should be <= 6
      nextManager should be(new RandomnessManager())
    }
/*
    "finish the game loop immediately when the current player already has all landmarks" in silence {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0, money = 100)),
        CurrentTurnPlayerId = 0,
        cardStacks = List(cardStack(1, weizenfeld)),
        inputManager = new InputManager(inputs = List("next")),
        rndManager = new RandomnessManager(numbers = List(1, 2))
      ).giveCard(0, bahnhof)
        .giveCard(0, freizeitpark)
        .giveCard(0, funkturm)
        .giveCard(0, einkaufszentrum)

      main.gameloop(state)
    }
*/
    "cover main by playing until player one buys all landmarks" in silence {
      val scriptedInput = List(
        "Bahnhof",
        "next",
        "1", "Freizeitpark",
        "next",
        "1", "Funkturm",
        "next",
        "1", "y", "Einkaufszentrum"
      ).mkString("\n") + "\n"

      Console.withIn(new ByteArrayInputStream(scriptedInput.getBytes("UTF-8"))) {
        main.main(Array.empty)
      }
    }
  }
}
