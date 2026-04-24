import de.htwg.se.machikoro.remake.debugInputManager
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.Queue

class DebugInputManagerTest extends AnyWordSpec with Matchers {

  "debugInputManager" should {
    "read values from the simulated input queue in order" in {
      debugInputManager.InputQueue = Queue("1", "2", "next")

      debugInputManager.readForTestAndGamePurposes("ignored") should be("1")
      debugInputManager.readForTestAndGamePurposes("ignored") should be("2")
      debugInputManager.readForTestAndGamePurposes("ignored") should be("next")
      debugInputManager.InputQueue.isEmpty should be(true)
    }

    "writeIntoSimulatedChat should enqueue hello" in {
      debugInputManager.InputQueue = Queue.empty

      debugInputManager.writeIntoSimulatedChat("anything")

      debugInputManager.InputQueue.nonEmpty should be(true)
      debugInputManager.readForTestAndGamePurposes("ignored") should be("hello")
    }
  }
}
