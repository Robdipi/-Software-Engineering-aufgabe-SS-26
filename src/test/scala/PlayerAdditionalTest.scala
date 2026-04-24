import de.htwg.se.machikoro.remake.{Gamestate, Player}
import de.htwg.se.machikoro.remake.allCardsBaseGame.*
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerAdditionalTest extends AnyWordSpec with Matchers {

  "Player" should {
    "only win after owning all four landmarks" in {
      val emptyPlayer = new Player(playerId = 0)
      emptyPlayer.hasWonTheGame() should be(false)

      val state = new Gamestate(Players = List(emptyPlayer))
        .giveCard(0, bahnhof)
        .giveCard(0, freizeitpark)
        .giveCard(0, funkturm)
        .giveCard(0, einkaufszentrum)

      state.Players.find(_.playerId == 0).value.hasWonTheGame() should be(true)
    }

    "activate green cards only for the rolling owner" in {
      val state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1)))
        .giveCard(0, baeckerei)

      val changedByOwner = state.activateCards(2, 0)
      changedByOwner.Players.find(_.playerId == 0).value.money should be(1)

      val changedByOtherPlayer = state.activateCards(2, 1)
      changedByOtherPlayer.Players.find(_.playerId == 0).value.money should be(0)
    }

    "activate blue cards even when another player rolled" in {
      val state = new Gamestate(Players = List(new Player(playerId = 0), new Player(playerId = 1)))
        .giveCard(0, bauernhof)

      val changed = state.activateCards(2, 1)

      changed.Players.find(_.playerId == 0).value.money should be(1)
      changed.Players.find(_.playerId == 1).value.money should be(0)
    }

    "not activate a card when the roll number does not match" in {
      val state = new Gamestate(Players = List(new Player(playerId = 0)))
        .giveCard(0, bergwerk)

      val changed = state.activateCards(8, 0)

      changed.Players.find(_.playerId == 0).value.money should be(0)
    }
  }
}
