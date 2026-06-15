package de.htwg.se.machikoro.remake.controller.mementoPatern.Memento.implXml

import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.commandPattern.UndoManagerInterface
import de.htwg.se.machikoro.remake.controller.commandPattern.impl1.UndoManager
import de.htwg.se.machikoro.remake.controller.mementoPatern.{MementoConstatants, MementoIntervace}
import de.htwg.se.machikoro.remake.model.Data.{Card, Gamestate, Player, AllCardsBaseGame, cardStack, turnState}
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
import scala.xml.{Elem, PrettyPrinter, XML}




case class MementoXml @Inject()(override val undoManager: UndoManagerInterface, override val safeFilePath: String) extends MementoIntervace{

  //Technically Flywheel pattern as it simplifies the saving of the card and reduces used
  // memory as only the name gets saved as a key for the concrete card
  val cardRegistry: Map[String, Card] =
    AllCardsBaseGame.getClass.getDeclaredFields
      .filter(f => f.getType == classOf[Card])
      .map { f =>
        f.setAccessible(true)
        val card = f.get(AllCardsBaseGame).asInstanceOf[Card]
        card.cardName -> card
      }
      .toMap

  private def playerToXml(p: Player): Elem = {
    <player>
      <id>
        {p.playerId}
      </id>
      <money>
        {p.money}
      </money>
      <getsAnotherTurn>
        {p.GetsAnotherTurn}
      </getsAnotherTurn>

      <properties>
        {p.properties.map(c => <card>
        {c.cardName}
      </card>)}
      </properties>
    </player>
  }

  private def cardStackToXml(cs: cardStack): Elem = {
    <cardStack>
      <amount>
        {cs.amount}
      </amount>
      <card>
        {cs.stackCard.cardName}
      </card>
    </cardStack>
  }

  private def gamestateToXml(state: Gamestate): Elem = {
    <gamestate>
      <curentTurn>
        {state.curentTurn}
      </curentTurn>
      <currentTurnPlayerId>
        {state.CurrentTurnPlayerId}
      </currentTurnPlayerId>
      <diceResult>
        {state.DiceResult}
      </diceResult>
      <diceChosen>
        {state.diceChoosen}
      </diceChosen>
      <state>
        {state.state.toString}
      </state>

      <players>
        {state.Players.map(playerToXml)}
      </players>

      <cardStacks>
        {state.cardStacks.map(cardStackToXml)}
      </cardStacks>
    </gamestate>
  }


  def create(gamestate: Gamestate, undoManager: UndoManagerInterface): MementoXml = {
    print("create")
    val xml: Elem = gamestateToXml(gamestate)
    val prettyXml = new PrettyPrinter(120, 2).format(xml)

    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    val timestamp: String = now.format(formatter)
    val path = Paths.get(
      MementoConstatants.SAVEFILE_FOLDER + "/" + timestamp + ".xml"
    )
    Files.write(path, prettyXml.getBytes("UTF-8"))
    MementoXml(undoManager, path.toString)
  }

  private def playerFromXml(node: scala.xml.Node): Option[Player] = {
    val maybeCards: List[Option[Card]] =
      (node \ "properties" \ "card").map(n =>
        cardRegistry.get(n.text.trim)
      ).toList

    if (maybeCards.exists(_.isEmpty)) {
      None
    } else {
      val cards: List[Card] = maybeCards.flatten

      Some(
        Player(
          playerId = (node \ "id").text.trim.toInt,
          money = (node \ "money").text.trim.toInt,
          GetsAnotherTurn = (node \ "getsAnotherTurn").text.trim.toBoolean,
          properties = cards
        )
      )
    }
  }
  private def cardStackFromXml(node: scala.xml.Node): Option[cardStack] = {
    val cardName = (node \ "card").text.trim
    val amount = (node \ "amount").text.trim.toInt

    cardRegistry.get(cardName).map { card =>
      cardStack(
        amount = amount,
        stackCard = card
      )
    }
  }

  def restore(): Option[Gamestate] = {
    val filePath = Paths.get(safeFilePath)
    if (!Files.exists(filePath)) return None

    val xml = XML.loadFile(filePath.toFile)

    val players: List[Player] = (xml \\ "player").toList.flatMap(playerFromXml)
    val stacks: List[cardStack] = (xml \\ "cardStack").toList.flatMap(cardStackFromXml)

    Some(
      Gamestate(
        curentTurn = (xml \ "curentTurn").text.trim.toInt,
        Players = players,
        CurrentTurnPlayerId = (xml \ "currentTurnPlayerId").text.trim.toInt,
        DiceResult = (xml \ "diceResult").text.trim.toInt,
        diceChoosen = (xml \ "diceChosen").text.trim.toInt,
        cardStacks = stacks,
        state = turnState.valueOf((xml \ "state").text.trim)
      )
    )
  }
}

