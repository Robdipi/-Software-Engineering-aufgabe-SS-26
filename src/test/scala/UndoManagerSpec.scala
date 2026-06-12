import de.htwg.se.machikoro.remake.controller.commandPattern.Command
import de.htwg.se.machikoro.remake.controller.commandPattern.impl1.UndoManager
import de.htwg.se.machikoro.remake.model.Gamestate
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

    "return none when loading from an empty savefile list" in {
      new UndoManager().loadSavefiles(Nil) shouldBe None
    }
  }
}
