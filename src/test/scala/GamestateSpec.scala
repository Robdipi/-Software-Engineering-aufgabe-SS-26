import de.htwg.se.machikoro.remake.model.*
import de.htwg.se.machikoro.remake.model.Data.{Gamestate, Player, Type, cardStack, startMoneyPlayers, TurnState}
import de.htwg.se.machikoro.remake.model.Data.AllCardsBaseGame.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GamestateSpec extends AnyWordSpec with Matchers {

  private val p1 = Player(money = 10, playerId = 0)
  private val p2 = Player(money = 10, playerId = 1)
  private val p3 = Player(money = 10, playerId = 2)

  private def moneyOf(state: Gamestate, playerId: Int): Int =
    state.Players.find(_.playerId == playerId).get.money

  "Gamestate" should {

    "define the default start money" in {
      startMoneyPlayers shouldBe 100
    }

    "change money of a player" in {
      val result =
        Gamestate(Players = List(p1, p2)).changeMoneyOfPlayer(0, 5)

      moneyOf(result, 0) shouldBe 15
      moneyOf(result, 1) shouldBe 10
    }

    "leave other players unchanged when changing money" in {
      val result =
        Gamestate(Players = List(p1, p2, p3)).changeMoneyOfPlayer(0, 5)

      moneyOf(result, 0) shouldBe 15
      moneyOf(result, 1) shouldBe 10
      moneyOf(result, 2) shouldBe 10
    }

    "apply store bonus from einkaufszentrum" in {
      val player = Player(
        money = 10,
        playerId = 0,
        properties = List(einkaufszentrum.copy(cardOwnerId = 0))
      )

      val result =
        Gamestate(Players = List(player)).changeMoneyOfPlayer(0, 3, Type.Store)

      moneyOf(result, 0) shouldBe 14
    }

    "not apply store bonus for other card types" in {
      val player = Player(
        money = 10,
        playerId = 0,
        properties = List(einkaufszentrum.copy(cardOwnerId = 0))
      )

      val result =
        Gamestate(Players = List(player)).changeMoneyOfPlayer(0, 3, Type.Farm)

      moneyOf(result, 0) shouldBe 13
    }

    "transfer money between players" in {
      val result =
        Gamestate(Players = List(p1, p2)).transferMoneyBetweenPlayers(0, 1, 3)

      moneyOf(result, 0) shouldBe 7
      moneyOf(result, 1) shouldBe 13
    }

    "leave uninvolved players unchanged during normal money transfer" in {
      val result =
        Gamestate(Players = List(p1, p2, p3)).transferMoneyBetweenPlayers(0, 1, 3)

      moneyOf(result, 0) shouldBe 7
      moneyOf(result, 1) shouldBe 13
      moneyOf(result, 2) shouldBe 10
    }

    "transfer restaurant money with einkaufszentrum bonus" in {
      val taker = Player(
        money = 10,
        playerId = 1,
        properties = List(einkaufszentrum.copy(cardOwnerId = 1))
      )

      val result =
        Gamestate(Players = List(p1, taker))
          .transferMoneyBetweenPlayers(0, 1, 2, Type.Restaurants)

      moneyOf(result, 0) shouldBe 7
      moneyOf(result, 1) shouldBe 13
    }

    "leave uninvolved players unchanged during restaurant bonus transfer" in {
      val taker = Player(
        money = 10,
        playerId = 1,
        properties = List(einkaufszentrum.copy(cardOwnerId = 1))
      )

      val result =
        Gamestate(Players = List(p1, taker, p3))
          .transferMoneyBetweenPlayers(0, 1, 2, Type.Restaurants)

      moneyOf(result, 0) shouldBe 7
      moneyOf(result, 1) shouldBe 13
      moneyOf(result, 2) shouldBe 10
    }

    "not apply restaurant bonus when taker player does not exist" in {
      val state =
        Gamestate(Players = List(p1))

      val result =
        state.transferMoneyBetweenPlayers(0, 99, 2, Type.Restaurants)

      moneyOf(result, 0) shouldBe 8
      result.Players.size shouldBe 1
    }

    "not transfer money to self" in {
      val state =
        Gamestate(Players = List(p1))

      state.transferMoneyBetweenPlayers(0, 0, 5) shouldBe state
    }

    "steal from everyone" in {
      val result =
        Gamestate(Players = List(p1, p2, p3)).stealFromEveryone(0, 2)

      moneyOf(result, 0) shouldBe 14
      moneyOf(result, 1) shouldBe 8
      moneyOf(result, 2) shouldBe 8
    }

    "scale money by card type" in {
      val player = Player(
        money = 0,
        properties = List(
          weizenfeld.copy(cardOwnerId = 0),
          apfelgarten.copy(cardOwnerId = 0)
        ),
        playerId = 0
      )

      val result =
        Gamestate(Players = List(player)).changeMoneyOfPlayerScaleByType(0, Type.Farm, 2)

      result.Players.head.money shouldBe 4
    }

    "leave non owner unchanged when scaling money by card type" in {
      val owner = Player(
        money = 0,
        properties = List(
          weizenfeld.copy(cardOwnerId = 0),
          apfelgarten.copy(cardOwnerId = 0)
        ),
        playerId = 0
      )

      val other = Player(
        money = 10,
        properties = List(
          weizenfeld.copy(cardOwnerId = 1)
        ),
        playerId = 1
      )

      val result =
        Gamestate(Players = List(owner, other)).changeMoneyOfPlayerScaleByType(0, Type.Farm, 2)

      moneyOf(result, 0) shouldBe 4
      moneyOf(result, 1) shouldBe 10
    }

    "give a card to a player" in {
      val result =
        Gamestate(Players = List(p1)).giveCard(0, weizenfeld)

      result.Players.head.properties.size shouldBe 1
      result.Players.head.properties.head.cardOwnerId shouldBe 0
    }

    "leave players unchanged when giving card to unknown player" in {
      val state =
        Gamestate(Players = List(p1))

      state.giveCard(42, weizenfeld) shouldBe state
    }

    "activate cards for all players" in {
      val player0 = Player(
        money = 0,
        playerId = 0,
        properties = List(weizenfeld.copy(cardOwnerId = 0))
      )
      val player1 = Player(
        money = 0,
        playerId = 1,
        properties = List(weizenfeld.copy(cardOwnerId = 1))
      )

      val result =
        Gamestate(Players = List(player0, player1)).activateCards(1, 0)

      moneyOf(result, 0) shouldBe 1
      moneyOf(result, 1) shouldBe 1
    }

    "iterate turn to next player" in {
      val result =
        Gamestate(
          curentTurn = 0,
          Players = List(p1, p2),
          CurrentTurnPlayerId = 0
        ).iterateTurn()

      result.curentTurn shouldBe 1
      result.CurrentTurnPlayerId shouldBe 1
    }

    "keep current player when player gets another turn" in {
      val player =
        p1.copy(GetsAnotherTurn = true)

      val result =
        Gamestate(
          curentTurn = 0,
          Players = List(player, p2),
          CurrentTurnPlayerId = 0
        ).iterateTurn()

      result.curentTurn shouldBe 1
      result.CurrentTurnPlayerId shouldBe 0
      result.Players.find(_.playerId == 0).get.GetsAnotherTurn shouldBe false
    }

    "only clear the extra turn flag of the current player" in {
      val current =
        Player(money = 10, playerId = 0, GetsAnotherTurn = true)

      val other =
        Player(money = 10, playerId = 1, GetsAnotherTurn = true)

      val result =
        Gamestate(
          curentTurn = 0,
          Players = List(current, other),
          CurrentTurnPlayerId = 0
        ).iterateTurn()

      result.CurrentTurnPlayerId shouldBe 0
      result.Players.find(_.playerId == 0).get.GetsAnotherTurn shouldBe false
      result.Players.find(_.playerId == 1).get.GetsAnotherTurn shouldBe true
    }

    "advance safely when current player id does not exist" in {
      val result =
        Gamestate(
          curentTurn = 0,
          Players = List(p1, p2),
          CurrentTurnPlayerId = 99
        ).iterateTurn()

      result.curentTurn shouldBe 1
      result.CurrentTurnPlayerId shouldBe 0
    }


    "advance safely when no players exist" in {
      val result = Gamestate(curentTurn = 4).iterateTurn()

      result.curentTurn shouldBe 5
      result.Players shouldBe empty
      result.CurrentTurnPlayerId shouldBe 0
    }

    "detect if current player has won" in {
      val winner = Player(
        playerId = 0,
        properties = List(
          bahnhof.copy(cardOwnerId = 0),
          freizeitpark.copy(cardOwnerId = 0),
          funkturm.copy(cardOwnerId = 0),
          einkaufszentrum.copy(cardOwnerId = 0)
        )
      )

      Gamestate(Players = List(winner), CurrentTurnPlayerId = 0).currentPlayerHasWon() shouldBe true
      Gamestate(Players = List(p1), CurrentTurnPlayerId = 0).currentPlayerHasWon() shouldBe false
    }

    "return false when checking win condition for a missing current player" in {
      val winner = Player(
        playerId = 0,
        properties = List(
          bahnhof.copy(cardOwnerId = 0),
          freizeitpark.copy(cardOwnerId = 0),
          funkturm.copy(cardOwnerId = 0),
          einkaufszentrum.copy(cardOwnerId = 0)
        )
      )

      Gamestate(Players = List(winner), CurrentTurnPlayerId = 99).currentPlayerHasWon() shouldBe false
    }

    "remove a card from stack" in {
      val result =
        Gamestate(
          Players = List(p1),
          cardStacks = List(cardStack(5, weizenfeld))
        ).removeCardFromStack(weizenfeld)

      result.cardStacks.head.amount shouldBe 4
    }

    "not remove a card from non matching stack" in {
      val result =
        Gamestate(
          Players = List(p1),
          cardStacks = List(cardStack(5, bauernhof))
        ).removeCardFromStack(weizenfeld)

      result.cardStacks.head.amount shouldBe 5
    }

    "change simple state values" in {
      val state =
        Gamestate()

      state.changeState(TurnState.Buyphase).state shouldBe TurnState.Buyphase
      state.changeDiceChosen(2).diceChoosen shouldBe 2
      state.changeDiceResult(9).DiceResult shouldBe 9
      state.changePlayers(List(p1)).Players should contain(p1)
    }
  }
}
