package de.htwg.se.machikoro.remake.controller.mementoPatern.Memento.implJson

import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.commandPattern.UndoManagerInterface
import de.htwg.se.machikoro.remake.controller.mementoPatern.{MementoConstatants, MementoIntervace}
import de.htwg.se.machikoro.remake.model.Data.{AllCardsBaseGame, Card, Gamestate, Player, TurnState, cardStack}
import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*

import java.nio.file.{Files, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try

case class MementoJson @Inject()(override val undoManager: UndoManagerInterface, override val safeFilePath: String) extends MementoIntervace {

  val cardRegistry: Map[String, Card] =
    AllCardsBaseGame.getClass.getDeclaredFields
      .filter(_.getType == classOf[Card])
      .map { field =>
        field.setAccessible(true)
        val card = field.get(AllCardsBaseGame).asInstanceOf[Card]
        card.cardName -> card
      }
      .toMap

  given Decoder[Card] =
    Decoder.decodeString.emap { name =>
      cardRegistry.get(name) match
        case Some(card) => Right(card)
        case None =>
          markFileCorrupted()
          Right(AllCardsBaseGame.starterweizenfeld)
    }

  given Decoder[Player] = deriveDecoder
  given Decoder[cardStack] = deriveDecoder
  given Decoder[Gamestate] = deriveDecoder
  given Decoder[TurnState] = Decoder.decodeString.map { value =>
    Try(TurnState.valueOf(value)).getOrElse {
      markFileCorrupted()
      TurnState.StartofTurn
    }
  }

  given Encoder[Card] = Encoder.encodeString.contramap(_.cardName)
  given Encoder[TurnState] = Encoder.encodeString.contramap(_.toString)
  given Encoder[Player] = deriveEncoder
  given Encoder[cardStack] = deriveEncoder
  given Encoder[Gamestate] = deriveEncoder

  private def restoreCardOwners(state: Gamestate): Gamestate =
    state.copy(Players = state.Players.map { player =>
      player.copy(properties = player.properties.map(_.copy(cardOwnerId = player.playerId)))
    })

  def restore(): Option[Gamestate] = {
    val decoded = Try(Files.readString(Paths.get(safeFilePath))).toEither.flatMap(decode[Gamestate])
    decoded match {
      case Right(gameState) if !fileCorrupted => Some(restoreCardOwners(gameState))
      case _ =>
        markFileCorrupted()
        None
    }
  }

  def create(gamestate: Gamestate, undoManager: UndoManagerInterface): MementoJson = {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    val path = Paths.get(MementoConstatants.SAVEFILE_FOLDER, s"${now.format(formatter)}.json")
    Files.createDirectories(path.getParent)
    Files.writeString(path, gamestate.asJson.spaces2)
    MementoJson(undoManager, path.toString)
  }
}
