package AI_Generated_Tests

import de.htwg.se.machikoro.remake.controller.commandPattern.{Command, UndoManagerInterface}
import de.htwg.se.machikoro.remake.controller.main.*
import de.htwg.se.machikoro.remake.controller.main.impl1.{ControllerV2, DefaultWinCondition, minimalWinCondition}
import de.htwg.se.machikoro.remake.controller.mementoPatern.{MementoCareTakerInterface, MementoIntervace}
import de.htwg.se.machikoro.remake.model.Data.*
import de.htwg.se.machikoro.remake.model.Data.AllCardsBaseGame.*
import de.htwg.se.machikoro.remake.model.Data.TurnState.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ControllerV2Spec extends AnyWordSpec with Matchers {

  private class RecordingObserver extends ViewObserver {
    var states: Vector[Gamestate] = Vector.empty
    override def update(state: Gamestate): Unit = states = states :+ state
  }

  private case class FakeMemento(restored: Option[Gamestate]) extends MementoIntervace {
    override val undoManager: UndoManagerInterface = null
    override val safeFilePath: String = "fake-save"
    override def restore(): Option[Gamestate] = restored
    override def create(gamestate: Gamestate, undoManager: UndoManagerInterface): MementoIntervace =
      FakeMemento(Some(gamestate))
  }

  private class FakeCareTaker extends MementoCareTakerInterface {
    var createdFrom: Option[Gamestate] = None
    override def create(gamestate: Gamestate, undoManager: UndoManagerInterface): MementoIntervace = {
      createdFrom = Some(gamestate)
      FakeMemento(Some(gamestate))
    }
    override def flushSavefiles(): Unit = ()
    override def loadGamesave(undoManager: UndoManagerInterface): Option[Gamestate] = None
  }

  private class RecordingUndoManager extends UndoManagerInterface {
    var executedCommands = 0
    var undoCalls: List[Int] = Nil
    var deletedNames: List[String] = Nil
    override def doStep(gamestate: Gamestate, command: Command): Unit = {
      executedCommands += 1
      command.doStep(gamestate)
    }
    override def undoStep(gamestate: Gamestate, n: Int): Unit = undoCalls = n :: undoCalls
    override def delete(mementoName: String): Unit = deletedNames = mementoName :: deletedNames
    override def loadSavefiles(mementos: List[MementoIntervace]): Option[Gamestate] =
      mementos.headOption.flatMap(_.restore())
  }

  private val neverWins: WinCondition = new WinCondition {
    override def check(player: Player): Boolean = false
  }

  private def newController(winCondition: WinCondition = neverWins): (ControllerV2, RecordingObserver, RecordingUndoManager, FakeCareTaker) = {
    val undo = new RecordingUndoManager
    val care = new FakeCareTaker
    val controller = ControllerV2(winCondition, undo, care)
    val observer = new RecordingObserver
    controller.add(observer)
    (controller, observer, undo, care)
  }

  private def player(id: Int, money: Int = 10, cards: List[Card] = Nil): Player =
    Player(money = money, playerId = id, properties = cards.map(_.copy(cardOwnerId = id)))

  private def warningBeforeBuyphase(observer: RecordingObserver): TurnState = {
    observer.states.takeRight(2).map(_.state) should have size 2
    observer.states.takeRight(2).last.state shouldBe Buyphase
    observer.states.takeRight(2).head.state
  }

  "ControllerV2" should {

    "announce dice selection at turn start when the current player owns a station" in {
      val (controller, observer, _, _) = newController()
      val game = Gamestate(Players = List(player(0, cards = List(bahnhof))), CurrentTurnPlayerId = 0)

      controller.startTurn(game)

      observer.states.map(_.state).take(2) shouldBe Vector(StartofTurn, ChooseDiceAmount)
    }

    "roll immediately, activate cards and enter buy phase when no station is owned" in {
      val (controller, observer, _, _) = newController()
      val game = Gamestate(Players = List(player(0)), CurrentTurnPlayerId = 0)

      controller.startTurn(game)

      observer.states.head.state shouldBe StartofTurn
      observer.states.map(_.state) should contain(Result2)
      observer.states.last.state shouldBe Buyphase
      observer.states.exists(s => s.DiceResult >= 1 && s.DiceResult <= 6) shouldBe true
    }

    "ask for dice rejection after a roll when the current player owns the radio tower" in {
      val (controller, observer, _, _) = newController()
      val game = Gamestate(Players = List(player(0, cards = List(funkturm))), CurrentTurnPlayerId = 0)

      controller.startTurn(game)

      observer.states.last.state shouldBe AskForRejectionOfResult
      observer.states.map(_.state) should not contain Buyphase
    }

    "handle choosing two dice by delegating to the undo manager and publishing Result1 first" in {
      val (controller, observer, undo, care) = newController()
      val game = Gamestate(Players = List(player(0, cards = List(bahnhof))), CurrentTurnPlayerId = 0)

      controller.handleInput(ChooseDiceAmountInput(2), game)

      undo.executedCommands shouldBe 1
      care.createdFrom shouldBe Some(game)
      observer.states.head.state shouldBe Result1
      observer.states.head.diceChoosen shouldBe 2
    }

    "activate cards after an accepted dice result" in {
      val (controller, observer, _, _) = newController()
      val game = Gamestate(
        Players = List(player(0, money = 0, cards = List(weizenfeld))),
        CurrentTurnPlayerId = 0,
        DiceResult = 1
      )

      controller.handleInput(RejectDiceRollInput(false), game)

      observer.states.map(_.state) should contain(Result2)
      observer.states.last.state shouldBe Buyphase
      observer.states.exists(_.Players.head.money == 1) shouldBe true
    }

    "reroll rejected dice results before activating cards" in {
      val (controller, observer, _, _) = newController()
      val game = Gamestate(Players = List(player(0)), CurrentTurnPlayerId = 0, DiceResult = 99)

      controller.handleInput(RejectDiceRollInput(true), game)

      observer.states.head.state shouldBe Result2
      observer.states.head.DiceResult should be >= 1
      observer.states.head.DiceResult should be <= 6
      observer.states.last.state shouldBe Buyphase
    }

    "buy an affordable card, reduce stack and move to the next player" in {
      val (controller, observer, _, _) = newController()
      val game = Gamestate(
        Players = List(player(0, money = 10), player(1, money = 10)),
        CurrentTurnPlayerId = 0,
        cardStacks = List(cardStack(4, bahnhof))
      )

      controller.handleInput(BuyCardInput("bahnhof"), game)

      observer.states.exists { state =>
        state.CurrentTurnPlayerId == 1 &&
          state.cardStacks.head.amount == 3 &&
          state.Players.find(_.playerId == 0).exists(p => p.money == 6 && p.properties.exists(_.cardName == bahnhof.cardName))
      } shouldBe true
    }

    "end the turn without buying when next is entered with surrounding whitespace" in {
      val (controller, observer, _, _) = newController()
      val game = Gamestate(Players = List(player(0), player(1)), CurrentTurnPlayerId = 0)

      controller.handleInput(BuyCardInput("  next  "), game)

      observer.states.exists(_.CurrentTurnPlayerId == 1) shouldBe true
      observer.states.map(_.state) should contain(StartofTurn)
      observer.states.map(_.state) should not contain NONE_EXISTANT_CARDNAME_WARNING
    }


    "announce a win instead of starting another turn after next" in {
      val winner = player(0, cards = List(bahnhof, freizeitpark, funkturm, einkaufszentrum))
      val (controller, observer, _, _) = newController(new DefaultWinCondition)

      controller.handleInput(BuyCardInput("next"), Gamestate(Players = List(winner), CurrentTurnPlayerId = 0))

      observer.states.map(_.state) should contain(PlayerWins)
      observer.states.map(_.state) should not contain ChooseDiceAmount
    }

    "delegate undo commands encoded in buy input" in {
      val (controller, observer, undo, _) = newController()
      val game = Gamestate(Players = List(player(0)), CurrentTurnPlayerId = 0)

      controller.handleInput(BuyCardInput("undo3"), game)

      undo.undoCalls shouldBe List(3)
      observer.states shouldBe empty
    }

    "allow observers to be removed" in {
      val undo = new RecordingUndoManager
      val care = new FakeCareTaker
      val controller = ControllerV2(neverWins, undo, care)
      val observer = new RecordingObserver
      controller.add(observer)
      controller.remove(observer)

      controller.startTurn(Gamestate(Players = List(player(0)), CurrentTurnPlayerId = 0))

      observer.states shouldBe empty
    }

    "evaluate default and minimal win conditions" in {
      val fullWinner = player(0, cards = List(bahnhof, freizeitpark, funkturm, einkaufszentrum))
      val smallWinner = player(0, cards = List(bahnhof, funkturm))

      DefaultWinCondition().check(fullWinner) shouldBe true
      DefaultWinCondition().check(smallWinner) shouldBe false
      minimalWinCondition().check(smallWinner) shouldBe true
      minimalWinCondition().check(player(1)) shouldBe false
    }
  }
}
