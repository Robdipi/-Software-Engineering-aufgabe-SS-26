package de.htwg.se.machikoro.remake.controller.mementoPatern.Memento.implXml
import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.commandPattern.UndoManagerInterface
import de.htwg.se.machikoro.remake.controller.mementoPatern.{mementoCareTakerInterface, mementoConstatants}
import de.htwg.se.machikoro.remake.model.Data.{Card, Gamestate, allCardsBaseGame}

import java.nio.file.{Files, Paths}
import scala.collection.JavaConverters.asScalaIteratorConverter
class mementoCareTakerXml  @Inject() extends mementoCareTakerInterface {
    //most stupid solution ever but I dont care. I like it everything else I tried doesn't work and I don't get it
    val theCreatorOfMementos = mementoXml(null, "( • ̀ω•́ )✧ dont open this") // I know I shouldn't use null
    val savefolderpath = Paths.get(mementoConstatants.savefilefolder)




    def create(gamestate: Gamestate, undoManager: UndoManagerInterface): mementoXml = {
      theCreatorOfMementos.create(gamestate, undoManager)
    }

    // delete all savefiles in
    def flushSavefiles(): Unit = {
      if (Files.exists(savefolderpath) && Files.isDirectory(savefolderpath)) {
        Files.list(savefolderpath).forEach(path => Files.delete(path))
      }
    }

    // writes all savefiles ordered as mementos into the undo queue and loads the lattest one
    def loadGamesave(undoManager: UndoManagerInterface): Option[Gamestate] = {
      if (Files.exists(savefolderpath) && Files.isDirectory(savefolderpath)) {
        val mementos = Files.list(savefolderpath)
          .iterator()
          .asScala
          .filter(_.toString.endsWith(".json"))
          .toSeq
          .sorted
          .map(path => mementoXml(undoManager, path.toString))
        undoManager.loadSavefiles(mementos.toList)
      } else {
        None
      }
    }

}
