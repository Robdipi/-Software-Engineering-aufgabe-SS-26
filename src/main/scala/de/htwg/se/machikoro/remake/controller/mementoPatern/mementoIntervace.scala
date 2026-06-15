package de.htwg.se.machikoro.remake.controller.mementoPatern

import de.htwg.se.machikoro.remake.controller.commandPattern.impl1.UndoManager
import de.htwg.se.machikoro.remake.controller.commandPattern.{Command, UndoManagerInterface}
import mementoConstatants.savefilefolder
import de.htwg.se.machikoro.remake.controller.mementoPatern.*
import de.htwg.se.machikoro.remake.controller.mementoPatern.Memento.implJson.mementoJson
import de.htwg.se.machikoro.remake.model.Data.{Card, Gamestate, allCardsBaseGame}
import de.htwg.se.machikoro.remake.model.Data.Type.{Dairy, Farm}
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
  val undoManager: UndoManagerInterface
  val safeFilePath : String
  var fileCorrupted = false
  def markFileCorrupted(): Unit = {fileCorrupted = true}
  
  def delete(): Unit = {
    Files.deleteIfExists(Paths.get(safeFilePath))
    undoManager.delete("safeFilePath")
  }
  def restore(): Option[Gamestate]
  def create(gamestate: Gamestate, undoManager: UndoManagerInterface): mementoIntervace
}


trait mementoCareTakerInterface () {
  def create(gamestate: Gamestate, undoManager: UndoManagerInterface): mementoIntervace
  def flushSavefiles(): Unit
  def loadGamesave(undoManager: UndoManagerInterface): Option[Gamestate]

  
}
