package de.htwg.se.machikoro.remake.controller.mementoPatern.Memento.implJson

import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.commandPattern.impl1.UndoManager
import de.htwg.se.machikoro.remake.controller.commandPattern.{Command, UndoManagerInterface}
import de.htwg.se.machikoro.remake.controller.mementoPatern.mementoConstatants.savefilefolder
import de.htwg.se.machikoro.remake.controller.mementoPatern.{mementoConstatants, mementoIntervace}
import de.htwg.se.machikoro.remake.model.Data.{Card, Gamestate, Player, allCardsBaseGame, cardStack, turnState}
import de.htwg.se.machikoro.remake.model.Data.Type.{Dairy, Farm}
import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*

import java.nio.file.{Files, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try


case class mementoJson @Inject()(override val undoManager: UndoManagerInterface, override val safeFilePath: String) extends mementoIntervace {

  //Technically Flywheel pattern as it simplifies the saving of the card and reduces used
  // memory as only the name gets saved as a key for the concrete card
  val cardRegistry: Map[String, Card] =
    allCardsBaseGame.getClass.getDeclaredFields
      .filter(f => f.getType == classOf[Card])
      .map { f =>
        f.setAccessible(true)
        val card = f.get(allCardsBaseGame).asInstanceOf[Card]
        card.cardName -> card
      }
      .toMap

  given Decoder[Card] =
    Decoder.decodeString.emap { name =>
      cardRegistry.get(name) match
        case Some(card) => Right(card)
        case None =>
          println(s"Warning: deleted safefile because of corrupted card: $name")
          markFileCorrupted()
          Right(allCardsBaseGame.starterweizenfeld)
    }

  given Decoder[Player] = deriveDecoder
  given Decoder[cardStack] = deriveDecoder
  given Decoder[Gamestate] = deriveDecoder
  given Decoder[turnState] = Decoder.decodeString.map { str =>
    Try(turnState.valueOf(str)).getOrElse {
      println(s"Warning: deleted safefile because of corrupted turnstate")
      markFileCorrupted()
      turnState.StartofTurn
    }
  }

  
  given Encoder[Card] = Encoder.encodeString.contramap(_.cardName)
  given Encoder[turnState] = Encoder.encodeString.contramap(_.toString)
  given Encoder[Player] = deriveEncoder
  given Encoder[cardStack] = deriveEncoder
  given Encoder[Gamestate] = deriveEncoder


  def restore(): Option[Gamestate] = {
    val json = Files.readString(Paths.get(safeFilePath))
    decode[Gamestate](json) match {
      case Right(gameState) => if(!fileCorrupted)Some(gameState) else None
      case Left(error) =>
        markFileCorrupted()
        None
    }
  }

  

  def create(gamestate: Gamestate, undoManager: UndoManagerInterface): mementoJson = {
    val jsonString = Json.obj("gamestate" -> gamestate.asJson).spaces2
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    val timestamp: String = now.format(formatter)
    val path = Paths.get(
      mementoConstatants.savefilefolder + "/" + timestamp + ".json"
    )
    Files.writeString(path, jsonString)
    mementoJson(undoManager, path.toString)
  }

}

