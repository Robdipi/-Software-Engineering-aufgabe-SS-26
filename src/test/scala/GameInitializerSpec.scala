package de.htwg.se.machikoro.remake.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameInitializerSpec extends AnyWordSpec with Matchers {

  "Game.apply" should {

    "create the standard game by default" in {
      val game = Game(2, "standart")

      game.Players.size shouldBe 2
      game.cardStacks.size shouldBe 19
    }

    "create the wheat hell game" in {
      val game = Game(2, "hell_of_weat")

      game.Players.size shouldBe 2
      game.cardStacks.size shouldBe 5
      game.cardStacks.head.amount shouldBe 100
    }
  }

  "initializeStandartGame" should {

    "create requested amount of players" in {
      initializeStandartGame().createGame(4).Players.size shouldBe 4
    }

    "give every player two start cards with correct owner ids" in {
      val game = initializeStandartGame().createGame(2)

      game.Players.foreach { player =>
        player.money shouldBe startMoneyPlayers
        player.properties.size shouldBe 2
        player.properties.map(_.cardOwnerId).distinct shouldBe List(player.playerId)
      }
    }

    "create all expected card stacks" in {
      val game = initializeStandartGame().createGame(2)
      val stackNames = game.cardStacks.map(_.stackCard.cardName)

      game.cardStacks.size shouldBe 19
      stackNames should contain allOf (
        "Weizenfeld",
        "Bauernhof",
        "Bäckerei",
        "Cafe",
        "Mini-Markt",
        "wald",
        "Bürohaus",
        "stadion",
        "Fernsehsender",
        "Molkerei",
        "Möbelfabrik",
        "Familien-Restaurant",
        "Bergwerk",
        "apfelgarten",
        "Markthalle",
        "Bahnhof",
        "Einkaufszentrum",
        "Freizeitpark",
        "Funkturm"
      )
    }

    "create unavailable Bürohaus stack with amount zero" in {
      val game = initializeStandartGame().createGame(2)
      val bueroStack = game.cardStacks.find(_.stackCard.cardName == "Bürohaus").get

      bueroStack.amount shouldBe 0
    }
  }

  "initializeWeatHell" should {

    "create wheat hell setup" in {
      val game = initializeWeatHell().createGame(2)

      game.cardStacks.head.stackCard.cardName shouldBe "Weizenfeld"
      game.cardStacks.head.amount shouldBe 100
      game.cardStacks.map(_.stackCard.cardName) should contain allOf (
        "Weizenfeld",
        "Bahnhof",
        "Einkaufszentrum",
        "Freizeitpark",
        "Funkturm"
      )
    }
  }
}
