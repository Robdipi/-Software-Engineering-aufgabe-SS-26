import de.htwg.se.machikoro.remake.controller.commandPattern.{Command, UndoManagerInterface}
import de.htwg.se.machikoro.remake.controller.mementoPatern.Memento.implJson.{MementoCreatorJson, MementoJson}
import de.htwg.se.machikoro.remake.controller.mementoPatern.Memento.implXml.{MementoCareTakerXml, MementoXml}
import de.htwg.se.machikoro.remake.controller.mementoPatern.{MementoConstatants, MementoIntervace}
import de.htwg.se.machikoro.remake.model.Data.*
import de.htwg.se.machikoro.remake.model.Data.AllCardsBaseGame.*
import de.htwg.se.machikoro.remake.model.Data.TurnState.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.nio.file.{Files, Path, Paths}
import scala.jdk.CollectionConverters.*

class MementoSpec extends AnyWordSpec with Matchers with BeforeAndAfterEach {

  private class RecordingUndoManager extends UndoManagerInterface {
    var deletedNames: List[String] = Nil
    override def doStep(gamestate: Gamestate, command: Command): Unit = command.doStep(gamestate)
    override def undoStep(gamestate: Gamestate, n: Int): Unit = ()
    override def delete(mementoName: String): Unit = deletedNames = mementoName :: deletedNames
    override def loadSavefiles(mementos: List[MementoIntervace]): Option[Gamestate] =
      mementos.headOption.flatMap(_.restore())
  }

  private val saveDir: Path = Paths.get(MementoConstatants.SAVEFILE_FOLDER)

  override def beforeEach(): Unit = {
    Files.createDirectories(saveDir)
    Files.list(saveDir).iterator().asScala.foreach(Files.deleteIfExists)
  }

  override def afterEach(): Unit = {
    if (Files.exists(saveDir)) Files.list(saveDir).iterator().asScala.foreach(Files.deleteIfExists)
  }

  private def write(path: Path, content: String): Path = {
    Files.writeString(path, content)
    path
  }

  private def validJson(playerMoney: Int = 7, stateName: String = "Buyphase"): String =
    s"""
       |{
       |  "curentTurn" : 2,
       |  "Players" : [ {
       |    "money" : $playerMoney,
       |    "properties" : [ "Weizenfeld", "Bahnhof" ],
       |    "GetsAnotherTurn" : true,
       |    "playerId" : 0
       |  } ],
       |  "CurrentTurnPlayerId" : 0,
       |  "DiceResult" : 4,
       |  "diceChoosen" : 1,
       |  "cardStacks" : [ {
       |    "amount" : 3,
       |    "stackCard" : "Bäckerei"
       |  } ],
       |  "state" : "$stateName"
       |}
       |""".stripMargin

  "MementoJson" should {

    "restore a valid gamestate from json" in {
      val undo = new RecordingUndoManager
      val file = write(saveDir.resolve("valid.json"), validJson())

      val restored = MementoJson(undo, file.toString).restore().get

      restored.curentTurn shouldBe 2
      restored.Players.head.money shouldBe 7
      restored.Players.head.GetsAnotherTurn shouldBe true
      restored.Players.head.properties.map(_.cardName) shouldBe List("Weizenfeld", "Bahnhof")
      restored.cardStacks.head.amount shouldBe 3
      restored.cardStacks.head.stackCard.cardName shouldBe "Bäckerei"
      restored.state shouldBe Buyphase
    }

    "assign restored cards to the player who owns them" in {
      val undo = new RecordingUndoManager
      val file = write(saveDir.resolve("owners.json"), validJson())

      val restored = MementoJson(undo, file.toString).restore().get

      restored.Players.head.properties.map(_.cardOwnerId) shouldBe List(0, 0)
    }

    "create a json save that can be restored without a wrapper object" in {
      val undo = new RecordingUndoManager
      val original = Gamestate(
        curentTurn = 1,
        Players = List(Player(money = 9, playerId = 0, properties = List(weizenfeld.copy(cardOwnerId = 0)))),
        CurrentTurnPlayerId = 0,
        DiceResult = 3,
        state = Buyphase
      )

      val created = MementoJson(undo, "unused.json").create(original, undo)
      val restored = created.restore()

      restored.map(_.DiceResult) shouldBe Some(3)
      restored.flatMap(_.Players.headOption.map(_.money)) shouldBe Some(9)
      Files.deleteIfExists(Paths.get(created.safeFilePath)) shouldBe true
    }

    "return none and mark the memento when a stored card name is unknown" in {
      val undo = new RecordingUndoManager
      val file = write(saveDir.resolve("corrupt-card.json"), validJson().replace("Weizenfeld", "Unknown card"))
      val memento = MementoJson(undo, file.toString)

      memento.restore() shouldBe None
      memento.fileCorrupted shouldBe true
    }

    "return none and mark the memento when a stored turn state is unknown" in {
      val undo = new RecordingUndoManager
      val file = write(saveDir.resolve("corrupt-state.json"), validJson(stateName = "NoSuchState"))
      val memento = MementoJson(undo, file.toString)

      memento.restore() shouldBe None
      memento.fileCorrupted shouldBe true
    }

    "return none and mark the memento for malformed json" in {
      val undo = new RecordingUndoManager
      val file = write(saveDir.resolve("malformed.json"), "not-json")
      val memento = MementoJson(undo, file.toString)

      memento.restore() shouldBe None
      memento.fileCorrupted shouldBe true
    }

    "delete the backing file and notify the undo manager" in {
      val undo = new RecordingUndoManager
      val file = write(saveDir.resolve("to-delete.json"), validJson())
      val memento = MementoJson(undo, file.toString)

      memento.delete()

      Files.exists(file) shouldBe false
      undo.deletedNames shouldBe List(file.toString)
    }
  }

  "MementoCreatorJson" should {

    "flush all files from the save folder" in {
      write(saveDir.resolve("a.json"), validJson())
      write(saveDir.resolve("b.json"), validJson(9))

      MementoCreatorJson().flushSavefiles()

      Files.list(saveDir).iterator().asScala.toList shouldBe empty
    }

    "load the most recent json save through the undo manager" in {
      val undo = new RecordingUndoManager
      write(saveDir.resolve("2026-01-02.json"), validJson(2))
      write(saveDir.resolve("2026-01-01.json"), validJson(1))

      val restored = MementoCreatorJson().loadGamesave(undo)

      restored.map(_.Players.head.money) shouldBe Some(2)
    }

    "return none when the save folder does not exist" in {
      Files.list(saveDir).iterator().asScala.foreach(Files.deleteIfExists)
      Files.deleteIfExists(saveDir)

      MementoCreatorJson().loadGamesave(new RecordingUndoManager) shouldBe None
    }
  }


  "MementoCareTakerXml" should {

    "flush and load xml saves using the xml extension" in {
      val undo = new RecordingUndoManager
      write(saveDir.resolve("2026-01-01.xml"),
        """<gamestate><curentTurn>0</curentTurn><currentTurnPlayerId>0</currentTurnPlayerId><diceResult>1</diceResult><diceChosen>1</diceChosen><state>StartofTurn</state><players><player><id>0</id><money>1</money><getsAnotherTurn>false</getsAnotherTurn><properties></properties></player></players><cardStacks></cardStacks></gamestate>""")
      write(saveDir.resolve("2026-01-02.xml"),
        """<gamestate><curentTurn>0</curentTurn><currentTurnPlayerId>0</currentTurnPlayerId><diceResult>1</diceResult><diceChosen>1</diceChosen><state>StartofTurn</state><players><player><id>0</id><money>2</money><getsAnotherTurn>false</getsAnotherTurn><properties></properties></player></players><cardStacks></cardStacks></gamestate>""")

      MementoCareTakerXml().loadGamesave(undo).map(_.Players.head.money) shouldBe Some(2)
      MementoCareTakerXml().flushSavefiles()
      Files.list(saveDir).iterator().asScala.toList shouldBe empty
    }
  }

  "MementoXml" should {

    "create and restore an xml save file" in {
      val undo = new RecordingUndoManager
      val game = Gamestate(
        curentTurn = 3,
        Players = List(Player(money = 12, playerId = 0, GetsAnotherTurn = true, properties = List(weizenfeld, bahnhof))),
        CurrentTurnPlayerId = 0,
        DiceResult = 5,
        diceChoosen = 2,
        cardStacks = List(cardStack(6, cafe)),
        state = Result2
      )

      val created = MementoXml(undo, saveDir.resolve("creator.xml").toString).create(game, undo)
      val restored = created.restore().get

      restored.curentTurn shouldBe 3
      restored.CurrentTurnPlayerId shouldBe 0
      restored.DiceResult shouldBe 5
      restored.diceChoosen shouldBe 2
      restored.state shouldBe Result2
      restored.Players.head.money shouldBe 12
      restored.Players.head.GetsAnotherTurn shouldBe true
      restored.Players.head.properties.map(_.cardName) shouldBe List("Weizenfeld", "Bahnhof")
      restored.cardStacks.head.stackCard.cardName shouldBe cafe.cardName
    }

    "return none when the xml file is missing" in {
      MementoXml(new RecordingUndoManager, saveDir.resolve("missing.xml").toString).restore() shouldBe None
    }

    "return none instead of throwing for malformed xml" in {
      val file = write(saveDir.resolve("malformed.xml"), "<gamestate><broken>")

      MementoXml(new RecordingUndoManager, file.toString).restore() shouldBe None
    }

    "preserve card owner ids while restoring xml" in {
      val file = write(
        saveDir.resolve("owners.xml"),
        """
          |<gamestate>
          |  <curentTurn>0</curentTurn><currentTurnPlayerId>2</currentTurnPlayerId>
          |  <diceResult>1</diceResult><diceChosen>1</diceChosen><state>StartofTurn</state>
          |  <players><player><id>2</id><money>5</money><getsAnotherTurn>false</getsAnotherTurn>
          |    <properties><card>Weizenfeld</card></properties>
          |  </player></players>
          |  <cardStacks></cardStacks>
          |</gamestate>
          |""".stripMargin
      )

      val restored = MementoXml(new RecordingUndoManager, file.toString).restore().get

      restored.Players.head.properties.head.cardOwnerId shouldBe 2
    }

    "skip corrupted players and stacks with unknown card names while restoring xml" in {
      val file = write(
        saveDir.resolve("partial.xml"),
        """
          |<gamestate>
          |  <curentTurn>1</curentTurn>
          |  <currentTurnPlayerId>0</currentTurnPlayerId>
          |  <diceResult>2</diceResult>
          |  <diceChosen>1</diceChosen>
          |  <state>StartofTurn</state>
          |  <players>
          |    <player><id>0</id><money>5</money><getsAnotherTurn>false</getsAnotherTurn><properties><card>Weizenfeld</card></properties></player>
          |    <player><id>1</id><money>5</money><getsAnotherTurn>false</getsAnotherTurn><properties><card>NoCard</card></properties></player>
          |  </players>
          |  <cardStacks>
          |    <cardStack><amount>4</amount><card>Bahnhof</card></cardStack>
          |    <cardStack><amount>4</amount><card>NoCard</card></cardStack>
          |  </cardStacks>
          |</gamestate>
          |""".stripMargin
      )

      val restored = MementoXml(new RecordingUndoManager, file.toString).restore().get

      restored.Players.map(_.playerId) shouldBe List(0)
      restored.cardStacks.map(_.stackCard.cardName) shouldBe List("Bahnhof")
    }
  }
}
