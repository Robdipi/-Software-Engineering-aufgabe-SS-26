package de.htwg.se.machikoro.remake.controller.mementoPatern.Memento.implXml
import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.commandPattern.UndoManagerInterface
import de.htwg.se.machikoro.remake.controller.mementoPatern.{MementoCareTakerInterface, MementoConstatants}
import de.htwg.se.machikoro.remake.model.Data.{Card, Gamestate, AllCardsBaseGame}

import java.nio.file.{Files, Paths}
import scala.collection.JavaConverters.asScalaIteratorConverter
class MementoCareTakerXml  @Inject() extends MementoCareTakerInterface {
    //most stupid solution ever but I don't care. I like it everything else I tried doesn't work and I don't get it
    val theCreatorOfMementos = MementoXml(null, "( • ̀ω•́ )✧ don't open this") // I know I shouldn't use null
    val savefolderpath = Paths.get(MementoConstatants.SAVEFILE_FOLDER)


  /**
   * Create a new Memento from a Gamestate and UndoManager
   * @param gamestate the gamestate
   *                  
   */
    def create(gamestate: Gamestate, undoManager: UndoManagerInterface): MementoXml = {
      theCreatorOfMementos.create(gamestate, undoManager)
    }


  /**
   * Delete all savefiles
   */
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
          .map(path => MementoXml(undoManager, path.toString))
        undoManager.loadSavefiles(mementos.toList)
      } else {
        None
      }
    }

}
