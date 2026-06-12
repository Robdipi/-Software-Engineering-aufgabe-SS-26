package de.htwg.se.machikoro.remake.controller.mementoPatern

import de.htwg.se.machikoro.remake.controller.commandPattern.impl1.UndoManager
import de.htwg.se.machikoro.remake.controller.commandPattern.{Command, UndoManagerInterface}
import mementoConstatants.savefilefolder
import de.htwg.se.machikoro.remake.controller.mementoPatern.*
import de.htwg.se.machikoro.remake.controller.mementoPatern.implJson.mementoJson
import de.htwg.se.machikoro.remake.model.*
import de.htwg.se.machikoro.remake.model.Type.{Dairy, Farm}
import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*

import java.nio.file.{Files, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.util.Try


object mementoConstatants {
  val savefilefolder = "data/saves"
  val MAXIMUM_SAFEFILES = 100
}

trait mementoIntervace () {
  val undoManager: Option[UndoManager]
  val safeFilePath : String
  var fileCorrupted = false
  def markFileCorrupted(): Unit = {fileCorrupted = true}
  
  def delete(): Unit = {
    Files.deleteIfExists(Paths.get(safeFilePath))
    undoManager.foreach(_.delete("safeFilePath"))
  }
  def restore(): Option[Gamestate]
  def create(gamestate: Gamestate, undoManager: Option[UndoManager]): mementoIntervace
}



object mementoCreator {
  //most stupid solution ever but I dont care. I like it everything else I tried doesn't work and I don't get it
  val theCreatorOfMementos = mementoJson(None,"( • ̀ω•́ )✧ dont open this")
  val savefolderpath = Paths.get(mementoConstatants.savefilefolder)
  
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
  

  def create(gamestate: Gamestate, undoManager: Option[UndoManager]): mementoJson = {
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
        .map(path => mementoJson(Some(undoManager),path.toString))
      undoManager.loadSavefiles(mementos.toList)
    }else{
      None
    }
  }

}
