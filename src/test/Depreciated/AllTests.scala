package Depreciated

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.ByteArrayInputStream
import scala.Console
import scala.collection.immutable.Queue
@deprecated
class AllTests extends AnyWordSpec with Matchers {

  def baseState(): Gamestate =
    Gamestate(
      Players = List(Player(playerId = 0), Player(playerId = 1)),
      CurrentTurnPlayerId = 0,
      cardStacks = List(cardStack(2, weizenfeld))
    )

  

  "Gamestate core" should {
    "change money" in {
      val s = baseState()
      s.changeMoneyOfPlayer(0,5).Players.head.money should be(5)
    }

    "transfer money" in {
      val s = baseState()
      val r = s.transferMoneyBetweenPlayers(0,1,2)
      r.Players(1).money should be(2)
    }

    "steal from everyone" in {
      val s = baseState()
      val r = s.stealFromEveryone(0,1)
      r.Players.head.money should be(1)
    }

    "iterate turn" in {
      val s = baseState()
      s.iterateTurn().CurrentTurnPlayerId should be(1)
    }

    "give card" in {
      val s = baseState()
      s.giveCard(0,weizenfeld).Players.head.properties.nonEmpty should be(true)
    }

    "activate cards" in {
      val s = baseState().giveCard(0,weizenfeld)
      s.activateCards(1,0).Players.head.money should be(1)
    }
  }
/*
  "Gamestate input safe" should {
    "dice amount recursion safe" in {
      debugInputManager.InputQueue = Queue("x","1")
      new Gamestate().getDiceAmount() should be(1)
    }

    "ask rejection safe" in {
      debugInputManager.InputQueue = Queue("x","y")
      new Gamestate().askForRejection() should be(false)
    }
  }

  "Gamestate buy safe" should {
    "buy success" in {
      debugInputManager.InputQueue = Queue("Weizenfeld")
      val s = baseState().copy(Players = List(Player(10,playerId=0)))
      val r = s.askForCardToBuy()
      r.Players.head.money should be(9)
    }

    "buy skip" in {
      debugInputManager.InputQueue = Queue("next")
      baseState().askForCardToBuy() should be(baseState())
    }
  }
*/
  "Player" should {
    "win condition" in {
      val s = Gamestate(Players = List(Player(playerId=0)))
        .giveCard(0,bahnhof)
        .giveCard(0,freizeitpark)
        .giveCard(0,funkturm)
        .giveCard(0,einkaufszentrum)

      s.Players.head.hasWonTheGame() should be(true)
    }
  }

  "Card" should {
    "toString" in {
      val c = weizenfeld.copy(cardOwnerId=0)
      c.cardToString() should include ("Weizenfeld")
    }

    "activate effect" in {
      val s = Gamestate(Players = List(Player(playerId=0)))
      val r = weizenfeld.copy(cardOwnerId=0).activate(s)
      r.Players.head.money should be(1)
    }
  }
}
