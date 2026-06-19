package de.htwg.se.machikoro.remake.controller.mementoPatern.Memento.implJson

import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.commandPattern.UndoManagerInterface
import de.htwg.se.machikoro.remake.controller.mementoPatern.{MementoCareTakerInterface, MementoConstatants}
import de.htwg.se.machikoro.remake.model.Data.Gamestate

import java.nio.file.{Files, Path, Paths}
import scala.collection.JavaConverters.asScalaIteratorConverter


class MementoCreatorJson @Inject() extends MementoCareTakerInterface {
  //most stupid solution ever but I don't care. I like it everything else I tried doesn't work and I don't get it
  val theCreatorOfMementos = MementoJson(null, "( • ̀ω•́ )✧ don't open this") // I know I shouldn't use null
  val savefolderpath: Path = Paths.get(MementoConstatants.SAVEFILE_FOLDER)

  


  def create(gamestate: Gamestate, undoManager: UndoManagerInterface): MementoJson = {
    theCreatorOfMementos.create(gamestate, undoManager)
  }

  // delete all savefiles in
  def flushSavefiles(): Unit = {
    if (Files.exists(savefolderpath) && Files.isDirectory(savefolderpath)) {
      Files.list(savefolderpath).forEach(path => Files.delete(path))
    }
  }

  // writes all savefiles ordered as mementos into the undo queue and loads the latest one
  def loadGamesave(undoManager: UndoManagerInterface): Option[Gamestate] = {
    if (Files.exists(savefolderpath) && Files.isDirectory(savefolderpath)) {
      val mementos = Files.list(savefolderpath)
        .iterator()
        .asScala
        .filter(_.toString.endsWith(".json"))
        .toSeq
        .sorted
        .map(path => MementoJson(undoManager, path.toString))
      undoManager.loadSavefiles(mementos.toList)
    } else {
      None
    }
  }
}