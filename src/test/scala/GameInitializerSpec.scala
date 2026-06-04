package de.htwg.se.machikoro.remake.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameInitializerSpec extends AnyWordSpec with Matchers {

  "initializeStandartGame" should {

    "create requested amount of players" in {
      val game =
        initializeStandartGame().createGame(4)

      game.Players.size shouldBe 4
    }

    "give every player two start cards" in {
      val game =
        initializeStandartGame().createGame(2)

      game.Players.foreach { p =>
        p.properties.size shouldBe 2
      }
    }
  }

  "initializeWeatHell" should {

    "create wheat hell setup" in {
      val game =
        initializeWeatHell().createGame(2)

      game.cardStacks.head.amount shouldBe 100
    }
  }
}