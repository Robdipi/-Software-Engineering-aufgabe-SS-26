package de.htwg.se.machikoro.remake.controller

import de.htwg.se.machikoro.remake.model.Gamestate
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UndoManagerSpec extends AnyWordSpec with Matchers {

  class TestCommand extends Command {

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

    "undo commands" in {
      val manager = new UndoManager
      val command = new TestCommand

      manager.doStep(Gamestate(), command)
      manager.undoStep(Gamestate())

      command.didUndo shouldBe true
    }

    "handle undo on empty stack" in {
      noException should be thrownBy {
        new UndoManager().undoStep(Gamestate())
      }
    }
  }
}