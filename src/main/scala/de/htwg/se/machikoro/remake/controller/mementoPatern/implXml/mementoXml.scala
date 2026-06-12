package de.htwg.se.machikoro.remake.controller.mementoPatern.implXml



import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.commandPattern.*
import de.htwg.se.machikoro.remake.controller.commandPattern.impl1.UndoManager
import de.htwg.se.machikoro.remake.controller.mementoPatern.*
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




case class mementoXml@Inject()(override val undoManager: Option[UndoManagerInterface], override val safeFilePath: String) extends mementoIntervace{
  
  def restore(): Option[Gamestate] = {
   None
  }

  def create(gamestate: Gamestate, undoManager: Option[UndoManagerInterface]): mementoXml = {
   mementoXml(None,"skibidi")
  }
}

