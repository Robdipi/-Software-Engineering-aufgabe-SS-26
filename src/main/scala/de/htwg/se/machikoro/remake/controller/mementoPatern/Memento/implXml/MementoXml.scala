package de.htwg.se.machikoro.remake.controller.mementoPatern.Memento.implXml

import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.commandPattern.UndoManagerInterface
import de.htwg.se.machikoro.remake.controller.mementoPatern.{MementoConstatants, MementoIntervace}
import de.htwg.se.machikoro.remake.model.Data.{AllCardsBaseGame, Card, Gamestate, Player, TurnState, cardStack}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try
import scala.xml.{Elem, PrettyPrinter, XML}

case class MementoXml @Inject()(override val undoManager: UndoManagerInterface, override val safeFilePath: String) extends MementoIntervace {

  val cardRegistry: Map[String, Card] =
    AllCardsBaseGame.getClass.getDeclaredFields
      .filter(_.getType == classOf[Card])
      .map { field =>
        field.setAccessible(true)
        val card = field.get(AllCardsBaseGame).asInstanceOf[Card]
        card.cardName -> card
      }
      .toMap

  private def playerToXml(player: Player): Elem =
    <player>
      <id>{player.playerId}</id>
      <money>{player.money}</money>
      <getsAnotherTurn>{player.GetsAnotherTurn}</getsAnotherTurn>
      <properties>{player.properties.map(card => <card>{card.cardName}</card>)}</properties>
    </player>

  private def cardStackToXml(stack: cardStack): Elem =
    <cardStack>
      <amount>{stack.amount}</amount>
      <card>{stack.stackCard.cardName}</card>
    </cardStack>

  private def gamestateToXml(state: Gamestate): Elem =
    <gamestate>
      <curentTurn>{state.curentTurn}</curentTurn>
      <currentTurnPlayerId>{state.CurrentTurnPlayerId}</currentTurnPlayerId>
      <diceResult>{state.DiceResult}</diceResult>
      <diceChosen>{state.diceChoosen}</diceChosen>
      <state>{state.state.toString}</state>
      <players>{state.Players.map(playerToXml)}</players>
      <cardStacks>{state.cardStacks.map(cardStackToXml)}</cardStacks>
    </gamestate>

  def create(gamestate: Gamestate, undoManager: UndoManagerInterface): MementoXml = {
    val prettyXml = new PrettyPrinter(120, 2).format(gamestateToXml(gamestate))
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
    val path = Paths.get(MementoConstatants.SAVEFILE_FOLDER, s"$timestamp.xml")
    Files.createDirectories(path.getParent)
    Files.write(path, prettyXml.getBytes(StandardCharsets.UTF_8))
    MementoXml(undoManager, path.toString)
  }

  private def playerFromXml(node: scala.xml.Node): Option[Player] = {
    val cards = (node \ "properties" \ "card").toList
      .map(node => cardRegistry.get(node.text.trim))

    if (cards.exists(_.isEmpty)) {
      None
    } else {
      val playerId = (node \ "id").text.trim.toInt
      Some(Player(
        playerId = playerId,
        money = (node \ "money").text.trim.toInt,
        GetsAnotherTurn = (node \ "getsAnotherTurn").text.trim.toBoolean,
        properties = cards.flatten.map(_.copy(cardOwnerId = playerId))
      ))
    }
  }

  private def cardStackFromXml(node: scala.xml.Node): Option[cardStack] = {
    val cardName = (node \ "card").text.trim
    cardRegistry.get(cardName).map { card =>
      cardStack(
        amount = (node \ "amount").text.trim.toInt,
        stackCard = card
      )
    }
  }

  def restore(): Option[Gamestate] = {
    val filePath = Paths.get(safeFilePath)
    if (!Files.isRegularFile(filePath)) {
      None
    } else {
      Try {
        val xml = XML.loadFile(filePath.toFile)
        val players = (xml \\ "player").toList.flatMap(playerFromXml)
        val stacks = (xml \\ "cardStack").toList.flatMap(cardStackFromXml)
        Gamestate(
          curentTurn = (xml \ "curentTurn").text.trim.toInt,
          Players = players,
          CurrentTurnPlayerId = (xml \ "currentTurnPlayerId").text.trim.toInt,
          DiceResult = (xml \ "diceResult").text.trim.toInt,
          diceChoosen = (xml \ "diceChosen").text.trim.toInt,
          cardStacks = stacks,
          state = TurnState.valueOf((xml \ "state").text.trim)
        )
      }.toOption
    }
  }
}
