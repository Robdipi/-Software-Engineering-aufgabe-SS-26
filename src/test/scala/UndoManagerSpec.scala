package de.htwg.se.machikoro.remake.controller

import de.htwg.se.machikoro.remake.model.Gamestate
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UndoManagerSpec extends AnyWordSpec with Matchers {

  class TestCommand extends Command {
    var didDo = false
    var didUndo = false
    var didRedo = false

    override def doStep(gamestate: Gamestate): Unit =
      didDo = true

    override def undoStep(gamestate: Gamestate): Unit =
      didUndo = true

    override def redoStep(gamestate: Gamestate): Unit =
      didRedo = true
  }

  "UndoManager" should {

    "execute commands" in {
      val manager = new UndoManager
      val command = new TestCommand

      manager.doStep(Gamestate(), command)

      command.didDo shouldBe true
    }

    "undo commands" in {
      val manager = new UndoManager
      val command = new TestCommand

      manager.doStep(Gamestate(), command)
      manager.undoStep(Gamestate())

      command.didUndo shouldBe true
    }

    "undo multiple commands in reverse order" in {
      val manager = new UndoManager
      val command1 = new TestCommand
      val command2 = new TestCommand

      manager.doStep(Gamestate(), command1)
      manager.doStep(Gamestate(), command2)
      manager.undoStep(Gamestate())

      command2.didUndo shouldBe true
      command1.didUndo shouldBe false

      manager.undoStep(Gamestate())

      command1.didUndo shouldBe true
    }

    "handle undo on empty stack" in {
      noException should be thrownBy {
        new UndoManager().undoStep(Gamestate())
      }
    }
  }
}
