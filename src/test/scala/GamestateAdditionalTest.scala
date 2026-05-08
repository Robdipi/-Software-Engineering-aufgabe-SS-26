import de.htwg.se.machikoro.remake.{Gamestate, InputManager, Player, RandomnessManager, Type, cardStack, startMoneyPlayers}
import de.htwg.se.machikoro.remake.allCardsBaseGame.*
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.Queue

class GamestateAdditionalTest extends AnyWordSpec with Matchers {

  "Gamestate" should {
    "initialize the standard game with players, starter cards and card stacks" in {
      val state = new Gamestate().initializeStandartGame(2)

      state.Players.size should be(2)
      state.Players.find(_.playerId == 0).value.money should be(startMoneyPlayers)
      state.Players.find(_.playerId == 1).value.money should be(startMoneyPlayers)

      state.Players.find(_.playerId == 0).value.properties.map(_.cardName) should contain("Weizenfeld")
      state.Players.find(_.playerId == 0).value.properties.map(_.cardName) should contain("Bäckerei")
      state.Players.find(_.playerId == 1).value.properties.map(_.cardOwnerId).distinct should be(List(1))

      state.cardStacks.size should be(19)
      state.cardStacks.map(_.stackCard.cardName) should contain("Bahnhof")
      state.cardStacks.map(_.stackCard.cardName) should contain("Funkturm")
    }

    "giveCard should add a copy owned by the target player" in {
      val state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1)))

      val changed = state.giveCard(1, weizenfeld)

      changed.Players.find(_.playerId == 0).value.properties should be(empty)
      changed.Players.find(_.playerId == 1).value.properties.head.cardName should be("Weizenfeld")
      changed.Players.find(_.playerId == 1).value.properties.head.cardOwnerId should be(1)
    }

    "removeCardFromStack should reduce only the matching stack" in {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0)),
        cardStacks = List(cardStack(2, weizenfeld), cardStack(3, bauernhof))
      )

      val changed = state.removeCardFromStack(weizenfeld)

      changed.cardStacks.find(_.stackCard.cardName == "Weizenfeld").value.amount should be(1)
      changed.cardStacks.find(_.stackCard.cardName == "Bauernhof").value.amount should be(3)
    }

    "actuallyBuyCard should reduce money, remove one card from stack and give the card" in {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0, money = 10)),
        CurrentTurnPlayerId = 0,
        cardStacks = List(cardStack(2, weizenfeld))
      )

      val changed = state.actuallyBuyCard(weizenfeld, cardStack(2, weizenfeld), state.inputManager)

      changed.Players.find(_.playerId == 0).value.money should be(9)
      changed.Players.find(_.playerId == 0).value.properties.head.cardName should be("Weizenfeld")
      changed.cardStacks.find(_.stackCard.cardName == "Weizenfeld").value.amount should be(1)
    }

    "askForCardToBuy should return unchanged when input is next" in {


      val state = new Gamestate(
        Players = List(new Player(playerId = 0, money = 10)),
        CurrentTurnPlayerId = 0,
        cardStacks = List(cardStack(2, weizenfeld)),
        inputManager = new InputManager(inputs = List("next"))
      )

      state.askForCardToBuy(state.inputManager) should be(state)
    }

    "askForCardToBuy should buy a valid affordable card" in {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0, money = 10)),
        CurrentTurnPlayerId = 0,
        cardStacks = List(cardStack(2, weizenfeld)),
        inputManager = new InputManager(inputs = List("Weizenfeld"))
      )

      val changed = state.askForCardToBuy(state.inputManager)

      changed.Players.find(_.playerId == 0).value.money should be(9)
      changed.Players.find(_.playerId == 0).value.properties.head.cardName should be("Weizenfeld")
      changed.cardStacks.head.amount should be(1)
    }

    "getDiceAmount should accept one and two" in {
      val state = new Gamestate(inputManager = new InputManager(inputs = List("1","2")))

      val (diceamount1, state1) = state.getDiceAmount(state.inputManager)
      diceamount1 should be(1)
      val (diceamount2, state2) = state1.getDiceAmount(state1.inputManager)
      diceamount2 should be(2)
    }

    "askForRejection should map y to false and n to true" in {
      val state = new Gamestate(inputManager = new InputManager(inputs = List("y","n")))

      val (b1,inputManager1) = state.askForRejection(state.inputManager)
      b1 should be(false)
      val (b2,inputManager2) = state.askForRejection(inputManager1)
      b2 should be(true)
    }

    "choseDiceamount should roll one die when the player has no Bahnhof" in {

      val state = new Gamestate(Players = List(
        new Player(playerId = 0)), 
        CurrentTurnPlayerId = 0,
        rndManager = new RandomnessManager(numbers = List(4,6)))

      val changed = state.choseDiceamount()

      changed.diceChoosen should be(1)
      changed.DiceResult should be(4)
    }

    "choseDiceamount should roll two dice when Bahnhof allows it and input is 2" in {
      val state = new Gamestate(Players = List(
        new Player(playerId = 0)),
        CurrentTurnPlayerId = 0,
        inputManager = new InputManager(inputs = List("2")),
        rndManager = new RandomnessManager(numbers = List(3, 5)))
        .giveCard(0, bahnhof)
      
      val changed = state.choseDiceamount()
      changed.diceChoosen should be(2)
      changed.DiceResult should be(8)
    }

    "choseDiceamount should grant another turn on double dice with Freizeitpark" in {

      val state = new Gamestate(
        Players = List(new Player(playerId = 0)), 
        CurrentTurnPlayerId = 0,
        inputManager = new InputManager(inputs = List("2")),
        rndManager = new RandomnessManager(numbers = List(4, 4)))
        .giveCard(0, bahnhof)
        .giveCard(0, freizeitpark)

      val changed = state.choseDiceamount()

      changed.DiceResult should be(8)
      changed.Players.find(_.playerId == 0).value.GetsAnotherTurn should be(true)
    }

    "checkingResult should keep the result when the player accepts it" in {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0)),
        CurrentTurnPlayerId = 0,
        DiceResult = 5,
        inputManager = new InputManager(inputs = List("y")),
        rndManager = new RandomnessManager(numbers = List(5)))
        .giveCard(0, funkturm)
      
      val changed = state.checkingResult()
      changed.DiceResult should be(5)
    }

    "checkingResult should reroll one die when rejected" in {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0)),
        CurrentTurnPlayerId = 0,
        DiceResult = 1,
        diceChoosen = 1,
        inputManager = new InputManager(inputs = List("n")),
        rndManager = new RandomnessManager(numbers = List(6,2))
      ).giveCard(0, funkturm)

      val changed = state.checkingResult()

      changed.DiceResult should be(6)
    }

    "checkingResult should reroll two dice and grant another turn on double with Freizeitpark" in {
      val state = new Gamestate(
        Players = List(new Player(playerId = 0)),
        CurrentTurnPlayerId = 0,
        DiceResult = 3,
        diceChoosen = 2,
        inputManager = new InputManager(inputs = List("n")),
        rndManager = new RandomnessManager(numbers = List(2,2))
      ).giveCard(0, funkturm)
        .giveCard(0, freizeitpark)

      val changed = state.checkingResult()

      changed.DiceResult should be(4)
      changed.Players.find(_.playerId == 0).value.GetsAnotherTurn should be(true)
    }

    "currentPlayerHasWon should reflect the current player" in {
      val losingState = new Gamestate(Players = List(new Player(playerId = 0)), CurrentTurnPlayerId = 0)
      losingState.currentPlayerHasWon() should be(false)

      val winningState = losingState
        .giveCard(0, bahnhof)
        .giveCard(0, freizeitpark)
        .giveCard(0, funkturm)
        .giveCard(0, einkaufszentrum)

      winningState.currentPlayerHasWon() should be(true)
    }
  }
}
