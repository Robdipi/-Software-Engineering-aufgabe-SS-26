import de.htwg.se.machikoro.remake.randomNumberManager
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.Queue

class RandomNumberManagerTest extends AnyWordSpec with Matchers {

  "randomNumberManager" should {
    "return queued numbers in order" in {
      randomNumberManager.NumQueue = Queue.empty
      randomNumberManager.writeIntoSimulatedRandomness(2)
      randomNumberManager.writeIntoSimulatedRandomness(5)

      randomNumberManager.getNextRandomNumber() should be(2)
      randomNumberManager.getNextRandomNumber() should be(5)
    }

    "clamp queued numbers to dice range" in {
      randomNumberManager.NumQueue = Queue(-10, 0, 1, 6, 7, 99)

      randomNumberManager.getNextRandomNumber() should be(1)
      randomNumberManager.getNextRandomNumber() should be(1)
      randomNumberManager.getNextRandomNumber() should be(1)
      randomNumberManager.getNextRandomNumber() should be(6)
      randomNumberManager.getNextRandomNumber() should be(6)
      randomNumberManager.getNextRandomNumber() should be(6)
    }

    "return a real dice value when the queue is empty" in {
      randomNumberManager.NumQueue = Queue.empty

      val number = randomNumberManager.getNextRandomNumber()

      number should be >= 1
      number should be <= 6
    }
  }
}
