package AI_Generated_Tests

import de.htwg.se.machikoro.remake.controller.commandPattern.{Command, UndoManagerInterface}
import de.htwg.se.machikoro.remake.controller.commandPattern.impl1.UndoManager
import de.htwg.se.machikoro.remake.controller.mementoPatern.MementoIntervace
import de.htwg.se.machikoro.remake.model.Data.{Gamestate, Player}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UndoManagerSpec extends AnyWordSpec with Matchers {

  class TestCommand extends Command(null) {
    var didDo = false
    var didUndo = false
    var didRedo = false

    override def doStep(g: Gamestate): Unit =
      didDo = true

    override def undoStep(g: Gamestate): Unit =
      didUndo = true

    override def redoStep(g: Gamestate): Unit =
      didRedo = true
  }

  private def testMemento(restored: Option[Gamestate] = None): MementoIntervace = new MementoIntervace {
    override val undoManager: UndoManagerInterface = null
    override val safeFilePath: String = "test-memento"
    override def restore(): Option[Gamestate] = restored
    override def create(gamestate: Gamestate, undoManager: UndoManagerInterface): MementoIntervace = this
  }

  "UndoManager" should {

    "execute commands" in {
      val manager = new UndoManager
      val command = new TestCommand

      manager.doStep(Gamestate(), command)

      command.didDo shouldBe true
    }

    "undo one command" in {
      val manager = new UndoManager
      val command = new TestCommand

      manager.doStep(Gamestate(), command)
      manager.undoStep(Gamestate(), 1)

      command.didUndo shouldBe true
    }

    "undo multiple commands" in {
      val manager = new UndoManager
      val command1 = new TestCommand
      val command2 = new TestCommand

      manager.doStep(Gamestate(), command1)
      manager.doStep(Gamestate(), command2)
      manager.undoStep(Gamestate(), 2)

      command1.didUndo shouldBe true
      command2.didUndo shouldBe true
    }

    "handle undo on empty stack" in {
      noException should be thrownBy {
        new UndoManager().undoStep(Gamestate(), 1)
      }
    }

    "delete unknown memento name without throwing" in {
      noException should be thrownBy {
        new UndoManager().delete("unknown")
      }
    }

    "delete removes command preventing undo" in {
      val manager = new UndoManager
      var wasUndone = false
      val command = new Command(testMemento()) {
        override def doStep(g: Gamestate): Unit = ()
        override def undoStep(g: Gamestate): Unit = { wasUndone = true }
        override def redoStep(g: Gamestate): Unit = ()
      }
      manager.doStep(Gamestate(), command)
      manager.delete("test-memento")
      manager.undoStep(Gamestate(), 1)
      wasUndone shouldBe false
    }

    "return none when loading from an empty savefile list" in {
      new UndoManager().loadSavefiles(Nil) shouldBe None
    }

    "return restored state from loadSavefiles with valid mementos" in {
      val restored = Gamestate(Players = List(Player(money = 5, playerId = 0)))
      val memento = testMemento(Some(restored))
      new UndoManager().loadSavefiles(List(memento)) shouldBe Some(restored)
    }
  }
}
