package de.htwg.se.machikoro.remake.controller.mementoPatern

import de.htwg.se.machikoro.remake.controller.commandPattern.impl1.UndoManager
import de.htwg.se.machikoro.remake.controller.commandPattern.{Command, UndoManagerInterface}
import de.htwg.se.machikoro.remake.controller.mementoPatern.mementoConstatants.savefilefolder
import de.htwg.se.machikoro.remake.model.*
import de.htwg.se.machikoro.remake.model.Type.{Dairy, Farm}

import java.nio.file.{Files, Paths}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.*
import io.circe.syntax.*
import io.circe.generic.semiauto.*
import io.circe.parser.decode

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.util.Try


object mementoConstatants {
  val savefilefolder = "data/saves"
  val MAXIMUM_SAFEFILES = 100
}

case class memento (val undoManager: Option[UndoManager],val safeFilePath : String) {
  //Technically Flywheel pattern as it simplifies the saving of the card and reduces used
  // memory as only the name gets saved as a key for the concrete card
  var fileCorrupted = false
  val cardRegistry: Map[String, Card] =
    allCardsBaseGame.getClass.getDeclaredFields
      .filter(f => f.getType == classOf[Card])
      .map { f =>
        f.setAccessible(true)
        val card = f.get(allCardsBaseGame).asInstanceOf[Card]
        card.cardName -> card
      }
      .toMap
  private def markFileCorrupted(): Unit = {fileCorrupted = true}

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



  def delete(): Unit = {
    Files.deleteIfExists(Paths.get(safeFilePath)) // have to drop object somehow so garbage collector does its thing
    undoManager.foreach(_.delete("safeFilePath"))
  }
 
   def restore(): Option[Gamestate] = {
    val json = Files.readString(Paths.get(safeFilePath))
    decode[Gamestate](json) match {
      case Right(gameState) => if(!fileCorrupted)Some(gameState) else None
      case Left(error) =>
        markFileCorrupted()
        None
    }
  }

  

  def create(gamestate: Gamestate, undoManager: Option[UndoManager]): memento = {
    val jsonString = Json.obj("gamestate" -> gamestate.asJson).spaces2
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    val timestamp: String = now.format(formatter)
    val path = Paths.get(
      mementoConstatants.savefilefolder + "/" + timestamp + ".json"
    )
    Files.writeString(path, jsonString)
    memento(undoManager, path.toString)
  }

}



object mementoCreator {
  //most stupid solution ever but I dont care. I like it everything else I tried doesn't work and I don't get it
  val theCreatorOfMementos = memento(None,"( • ̀ω•́ )✧ dont open this")
  val savefolderpath = Paths.get(mementoConstatants.savefilefolder)
  
  
  
  def create(gamestate: Gamestate, undoManager: Option[UndoManager]): memento = {
    theCreatorOfMementos.create(gamestate, undoManager)
  }
  // delete all savefiles in
  def flushSavefiles() : Unit = {
    if (Files.exists(savefolderpath) && Files.isDirectory(savefolderpath)) {
      Files.list(savefolderpath).forEach(path => Files.delete(path))
    }
  }

  // writes all savefiles ordered as mementos into the undo queue and loads the lattest one
  def loadGamesave(undoManager: UndoManager): Option[Gamestate] = {
    if (Files.exists(savefolderpath) && Files.isDirectory(savefolderpath)) {
      val mementos = Files.list(savefolderpath)
        .iterator()
        .asScala
        .filter(_.toString.endsWith(".json"))
        .toSeq
        .sorted
        .map(path => memento(Some(undoManager),path.toString))
      undoManager.loadSavefiles(mementos.toList)
    }else{
      None
    }
  }

}
