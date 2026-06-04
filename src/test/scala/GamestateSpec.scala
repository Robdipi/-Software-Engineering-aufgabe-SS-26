package de.htwg.se.machikoro.remake.model

import de.htwg.se.machikoro.remake.model.allCardsBaseGame._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GamestateSpec extends AnyWordSpec with Matchers {

  val p1 = Player(money = 10, playerId = 0)
  val p2 = Player(money = 10, playerId = 1)

  "Gamestate" should {

    "change money of a player" in {
      val state = Gamestate(Players = List(p1, p2))

      val result = state.changeMoneyOfPlayer(0, 5)

      result.Players.head.money shouldBe 15
    }

    "transfer money between players" in {
      val state = Gamestate(Players = List(p1, p2))

      val result = state.transferMoneyBetweenPlayers(0, 1, 3)

      result.Players.find(_.playerId == 0).get.money shouldBe 7
      result.Players.find(_.playerId == 1).get.money shouldBe 13
    }

    "not transfer money to self" in {
      val state = Gamestate(Players = List(p1))

      val result = state.transferMoneyBetweenPlayers(0, 0, 5)

      result shouldBe state
    }

    "steal from everyone" in {
      val state = Gamestate(Players = List(p1, p2))

      val result = state.stealFromEveryone(0, 2)

      result.Players.find(_.playerId == 0).get.money shouldBe 12
      result.Players.find(_.playerId == 1).get.money shouldBe 8
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

      val state = Gamestate(Players = List(player))

      val result =
        state.changeMoneyOfPlayerScaleByType(0, Type.Farm, 2)

      result.Players.head.money shouldBe 4
    }

    "give a card to a player" in {
      val state = Gamestate(Players = List(p1))

      val result = state.giveCard(0, weizenfeld)

      result.Players.head.properties.size shouldBe 1
    }

    "remove a card from stack" in {
      val stack = cardStack(5, weizenfeld)

      val state = Gamestate(
        Players = List(p1),
        cardStacks = List(stack)
      )

      val result = state.removeCardFromStack(weizenfeld)

      result.cardStacks.head.amount shouldBe 4
    }

    "change state" in {
      val state = Gamestate()

      state.changeState(turnState.Buyphase).state shouldBe turnState.Buyphase
    }

    "change dice amount" in {
      Gamestate().changeDiceChosen(2).diceChoosen shouldBe 2
    }

    "change dice result" in {
      Gamestate().changeDiceResult(9).DiceResult shouldBe 9
    }

    "change players" in {
      val state = Gamestate()

      state.changePlayers(List(p1)).Players should contain(p1)
    }
  }
}