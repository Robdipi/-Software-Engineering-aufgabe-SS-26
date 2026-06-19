import de.htwg.se.machikoro.remake.controller.main.impl1.RandomnessManager
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RandomnessManagerSpec extends AnyWordSpec with Matchers {

  "RandomnessManager" should {

    "return predefined values" in {
      val rnd = RandomnessManager(List(2, 5, 6))

      val (v1, r1) = rnd.getNextNum
      val (v2, r2) = r1.getNextNum
      val (v3, _) = r2.getNextNum

      v1 shouldBe 2
      v2 shouldBe 5
      v3 shouldBe 6
    }

    "cycle through predefined values" in {
      val rnd = RandomnessManager(List(1, 2))

      val (_, r1) = rnd.getNextNum
      val (_, r2) = r1.getNextNum
      val (v3, r3) = r2.getNextNum

      v3 shouldBe 1
      r3.index shouldBe 1
    }

    "return values between one and six when no predefined numbers exist" in {
      val rnd = RandomnessManager()

      val (value, next) = rnd.getNextNum

      value should be >= 1
      value should be <= 6
      next shouldBe rnd
    }
  }
}
