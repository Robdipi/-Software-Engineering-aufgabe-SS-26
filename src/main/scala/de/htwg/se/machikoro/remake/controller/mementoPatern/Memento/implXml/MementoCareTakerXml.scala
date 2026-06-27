package de.htwg.se.machikoro.remake.controller.mementoPatern.Memento.implXml

import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.commandPattern.UndoManagerInterface
import de.htwg.se.machikoro.remake.controller.mementoPatern.{MementoCareTakerInterface, MementoConstatants}
import de.htwg.se.machikoro.remake.model.Data.Gamestate

import java.nio.file.{Files, Paths}
import scala.collection.JavaConverters.asScalaIteratorConverter

class MementoCareTakerXml @Inject() extends MementoCareTakerInterface {
  val theCreatorOfMementos = MementoXml(null, "( • ̀ω•́ )✧ don't open this")
  val savefolderpath = Paths.get(MementoConstatants.SAVEFILE_FOLDER)

  def create(gamestate: Gamestate, undoManager: UndoManagerInterface): MementoXml =
    theCreatorOfMementos.create(gamestate, undoManager)

  def flushSavefiles(): Unit = {
    if (Files.isDirectory(savefolderpath)) {
      val paths = Files.list(savefolderpath)
      try paths.forEach(path => Files.delete(path))
      finally paths.close()
    }
  }

  def loadGamesave(undoManager: UndoManagerInterface): Option[Gamestate] = {
    if (!Files.isDirectory(savefolderpath)) {
      None
    } else {
      val paths = Files.list(savefolderpath)
      try {
        val mementos = paths.iterator().asScala
          .filter(_.toString.endsWith(".xml"))
          .toSeq
          .sortBy(_.toString)(Ordering.String.reverse)
          .map(path => MementoXml(undoManager, path.toString))
        undoManager.loadSavefiles(mementos.toList)
      } finally paths.close()
    }
  }
}
