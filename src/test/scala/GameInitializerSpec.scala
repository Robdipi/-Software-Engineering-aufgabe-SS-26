import de.htwg.se.machikoro.remake.model.*
import de.htwg.se.machikoro.remake.model.Data.{Gamestate, startMoneyPlayers}
import de.htwg.se.machikoro.remake.model.Data.allCardsBaseGame.*
import de.htwg.se.machikoro.remake.model.initialization.*
import de.htwg.se.machikoro.remake.model.initialization.impl1.{Game, initializeStandartGame, initializeWeatHell}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameInitializerSpec extends AnyWordSpec with Matchers {

  private def stackAmount(game: Gamestate, cardName: String): Int =
    game.cardStacks.find(_.stackCard.cardName == cardName).get.amount

  private def stackNames(game: Gamestate): List[String] =
    game.cardStacks.map(_.stackCard.cardName)

  private def checkStartPlayers(game: Gamestate, expectedAmount: Int): Unit = {
    game.Players.size shouldBe expectedAmount
    game.Players.map(_.playerId) shouldBe (0 until expectedAmount).toList

    game.Players.foreach { player =>
      player.money shouldBe startMoneyPlayers
      player.properties.size shouldBe 2
      player.properties.map(_.cardName) should contain("Weizenfeld")
      player.properties.map(_.cardName) should contain("Bäckerei")
      player.properties.foreach(_.cardOwnerId shouldBe player.playerId)
    }
  }

  "initializeStandartGame" should {

    "create requested amount of players with correct start cards and start money" in {
      val game =
        initializeStandartGame().createGame(4)

      checkStartPlayers(game, 4)
    }

    "create all standard card stacks in correct order" in {
      val game =
        initializeStandartGame().createGame(2)

      stackNames(game) shouldBe List(
        weizenfeld.cardName,
        bauernhof.cardName,
        baeckerei.cardName,
        cafe.cardName,
        minimarkt.cardName,
        wald.cardName,
        buerohaus.cardName,
        stadion.cardName,
        fernsehsender.cardName,
        molkerei.cardName,
        möbelfabrik.cardName,
        familienRestaurant.cardName,
        bergwerk.cardName,
        apfelgarten.cardName,
        markthalle.cardName,
        bahnhof.cardName,
        einkaufszentrum.cardName,
        freizeitpark.cardName,
        funkturm.cardName
      )
    }

    "create standard card stacks with correct amounts" in {
      val game =
        initializeStandartGame().createGame(2)

      game.cardStacks.size shouldBe 19

      stackAmount(game, weizenfeld.cardName) shouldBe 6
      stackAmount(game, bauernhof.cardName) shouldBe 6
      stackAmount(game, baeckerei.cardName) shouldBe 6
      stackAmount(game, cafe.cardName) shouldBe 6
      stackAmount(game, minimarkt.cardName) shouldBe 6
      stackAmount(game, wald.cardName) shouldBe 6
      stackAmount(game, buerohaus.cardName) shouldBe 0
      stackAmount(game, stadion.cardName) shouldBe 4
      stackAmount(game, fernsehsender.cardName) shouldBe 4
      stackAmount(game, molkerei.cardName) shouldBe 6
      stackAmount(game, möbelfabrik.cardName) shouldBe 6
      stackAmount(game, familienRestaurant.cardName) shouldBe 6
      stackAmount(game, bergwerk.cardName) shouldBe 6
      stackAmount(game, apfelgarten.cardName) shouldBe 6
      stackAmount(game, markthalle.cardName) shouldBe 6
      stackAmount(game, bahnhof.cardName) shouldBe 4
      stackAmount(game, einkaufszentrum.cardName) shouldBe 4
      stackAmount(game, freizeitpark.cardName) shouldBe 4
      stackAmount(game, funkturm.cardName) shouldBe 4
    }

    "create an empty standard game when zero players are requested" in {
      val game =
        initializeStandartGame().createGame(0)

      game.Players shouldBe empty
      game.cardStacks.size shouldBe 19
    }
  }

  "initializeWeatHell" should {

    "create requested amount of players with correct start cards and start money" in {
      val game =
        initializeWeatHell().createGame(3)

      checkStartPlayers(game, 3)
    }

    "create wheat hell card stacks in correct order" in {
      val game =
        initializeWeatHell().createGame(2)

      stackNames(game) shouldBe List(
        weizenfeld.cardName,
        bahnhof.cardName,
        einkaufszentrum.cardName,
        freizeitpark.cardName,
        funkturm.cardName
      )
    }

    "create wheat hell card stacks with correct amounts" in {
      val game =
        initializeWeatHell().createGame(2)

      game.cardStacks.size shouldBe 5

      stackAmount(game, weizenfeld.cardName) shouldBe 100
      stackAmount(game, bahnhof.cardName) shouldBe 4
      stackAmount(game, einkaufszentrum.cardName) shouldBe 4
      stackAmount(game, freizeitpark.cardName) shouldBe 4
      stackAmount(game, funkturm.cardName) shouldBe 4
    }

    "create an empty wheat hell game when zero players are requested" in {
      val game =
        initializeWeatHell().createGame(0)

      game.Players shouldBe empty
      game.cardStacks.size shouldBe 5
    }
  }

  "Game factory" should {

    "create wheat hell game for hell_of_weat" in {
      val game =
        new Game().apply(2, "hell_of_weat")

      game.cardStacks.size shouldBe 5
      stackAmount(game, weizenfeld.cardName) shouldBe 100
    }

    "create standard game for standart" in {
      val game =
        new Game().apply(2, "standart")

      game.cardStacks.size shouldBe 19
      stackAmount(game, weizenfeld.cardName) shouldBe 6
    }

    "create standard game for an unknown game type" in {
      val game =
        new Game().apply(2, "unknown")

      game.cardStacks.size shouldBe 19
      stackAmount(game, weizenfeld.cardName) shouldBe 6
    }

    "be usable through gameInitializationSystem trait" in {
      val system: gameInitializationSystem =
        new Game()

      val game =
        system.apply(1, "standart")

      game.Players.size shouldBe 1
      game.cardStacks.size shouldBe 19
    }
  }
}
