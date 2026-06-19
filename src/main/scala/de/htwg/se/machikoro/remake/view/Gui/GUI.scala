package de.htwg.se.machikoro.remake.view.Gui

import com.google.inject.Inject
import de.htwg.se.machikoro.remake.controller.main.{BuyCardInput, ChooseDiceAmountInput, ControllerInterface, RejectDiceRollInput}
import de.htwg.se.machikoro.remake.model.Data.*
import de.htwg.se.machikoro.remake.model.Data.TurnState
import de.htwg.se.machikoro.remake.view.ViewInterface

import java.io.File

import scalafx.Includes.*
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.*
import scalafx.scene.text.{Font, FontWeight}

class GUI @Inject()(controller: ControllerInterface) extends ViewInterface {

  controller.add(this)

  private var currentState: Option[Gamestate] = None

  val root: BorderPane = new BorderPane()

  private val titleLabel = new Label("Machi Koro") {
    font = Font.font("Arial", FontWeight.Bold, 38)
    style = "-fx-text-fill: #0f172a;"
  }

  private val infoLabel = new Label("Spiel wird geladen ...") {
    font = Font.font("Arial", FontWeight.Bold, 16)
    style = "-fx-text-fill: #334155;"
  }

  private val diceLabel = new Label("🎲 -") {
    font = Font.font("Arial", FontWeight.Bold, 28)
    style = "-fx-text-fill: #1d4ed8;"
  }

  private val statusLabel = new Label("Willkommen bei Machi Koro") {
    font = Font.font("Arial", FontWeight.Bold, 22)
    wrapText = true
    alignment = Pos.Center
    maxWidth = 1000
    style = "-fx-text-fill: #111827;"
  }

  private val playersBox = new VBox(14) {
    padding = Insets(14)
    prefWidth = 330
    style = "-fx-background-color: rgba(255,255,255,0.75); -fx-background-radius: 18;"
  }

  private val marketGrid = new TilePane {
    hgap = 10
    vgap = 10
    padding = Insets(14)
    prefColumns = 5
    alignment = Pos.TopCenter
  }

  private val marketScroll = new ScrollPane {
    content = marketGrid
    fitToWidth = true
    pannable = true
    vbarPolicy = ScrollPane.ScrollBarPolicy.Never
    hbarPolicy = ScrollPane.ScrollBarPolicy.Never
    style =
      """
        |-fx-background-color: transparent;
        |-fx-background: transparent;
        |-fx-padding: 0;
      """.stripMargin
  }

  private val marketFrame = new StackPane {
    padding = Insets(10)
    style =
      """
        |-fx-background-color: rgba(255,255,255,0.45);
        |-fx-border-color: #64748b;
        |-fx-border-width: 2;
        |-fx-border-radius: 12;
        |-fx-background-radius: 12;
        |-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.14), 7, 0, 0, 2);
      """.stripMargin
    children = Seq(marketScroll)
  }

  private val actionBox = new HBox(12) {
    alignment = Pos.Center
    padding = Insets(14)
  }

  private val logArea = new TextArea {
    editable = false
    wrapText = true
    prefRowCount = 4
    maxHeight = 110
    style = "-fx-font-family: monospace; -fx-font-size: 12px;"
  }

  root.padding = Insets(16)
  root.style = "-fx-background-color: linear-gradient(to bottom right, #e0f2fe, #f8fafc 45%, #dcfce7);"
  root.top = new VBox(7, titleLabel, new HBox(18, infoLabel, diceLabel) {
    alignment = Pos.Center
  }, statusLabel) {
    alignment = Pos.Center
    padding = Insets(8, 8, 15, 8)
  }
  root.left = playersBox
  root.center = marketFrame
  root.bottom = new VBox(8, actionBox, logArea)

  override def update(state: Gamestate): Unit = {
    Platform.runLater {
      currentState = Some(state)
      render(state)
    }
  }

  private def render(state: Gamestate): Unit = {
    val currentPlayerNumber = state.CurrentTurnPlayerId + 1
    val money = state.Players.find(_.playerId == state.CurrentTurnPlayerId).map(_.money).getOrElse(0)

    infoLabel.text = s"Spieler $currentPlayerNumber ist dran  |  Münzen: $money  |  Runde: ${state.curentTurn + 1}"
    diceLabel.text = if (state.DiceResult > 0) s"🎲 ${state.DiceResult}" else "🎲 -"

    renderPlayers(state)
    renderMarket(state)
    renderActions(state)
    appendLogFor(state)
  }

  private def renderPlayers(state: Gamestate): Unit = {
    playersBox.children.clear()
    playersBox.children += new Label("Spieler") {
      font = Font.font("Arial", FontWeight.Bold, 25)
      style = "-fx-text-fill: #0f172a;"
    }

    state.Players.sortBy(_.playerId).foreach { player =>
      val isCurrent = player.playerId == state.CurrentTurnPlayerId
      val cardSummary = player.properties
        .groupBy(_.cardName)
        .toList
        .sortBy(_._1.toLowerCase)
        .map { case (name, cards) => s"• $name x${cards.size}" }
        .mkString("\n")

      playersBox.children += new VBox(7) {
        padding = Insets(12)
        style =
          if (isCurrent)
            "-fx-background-color: #dbeafe; -fx-border-color: #2563eb; -fx-border-width: 3; -fx-background-radius: 15; -fx-border-radius: 15;"
          else
            "-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-width: 1; -fx-background-radius: 15; -fx-border-radius: 15;"

        children = Seq(
          new Label(s"Spieler ${player.playerId + 1}${if isCurrent then "  ← dran" else ""}") {
            font = Font.font("Arial", FontWeight.Bold, 17)
            style = "-fx-text-fill: black;"
          },
          new Label(s"💰 ${player.money} Münzen") {
            font = Font.font("Arial", FontWeight.Bold, 14)
            style = "-fx-text-fill: black;"
          },
          new Label(landmarkText(player)) {
            wrapText = true
            style = "-fx-text-fill: #475569;"
          },
          new Label(if cardSummary.nonEmpty then cardSummary else "Noch keine Karten") {
            wrapText = true
            maxWidth = 285
            font = Font.font(12)
            style = "-fx-text-fill: black;"
          }
        )
      }
    }
  }

  private def landmarkText(player: Player): String = {
    Seq(
      "Bahnhof" -> player.canChooseDyeAmount(),
      "Einkaufszentrum" -> player.getExtraMoney(),
      "Freizeitpark" -> player.canGetAnotherTurn(),
      "Funkturm" -> player.canRejectDyeTrow()
    ).map { case (name, hasIt) => s"${if hasIt then "✓" else "□"} $name" }.mkString("  ")
  }

  private def renderMarket(state: Gamestate): Unit = {
    marketGrid.children.clear()

    state.cardStacks.foreach { stack =>
      val card = stack.stackCard
      val canBuy = state.state == TurnState.Buyphase && stack.amount > 0
      val dice = if card.roleNumbers.isEmpty then "-" else card.roleNumbers.mkString(", ")

      val buyButton = new Button(if canBuy then "Kaufen" else "Nicht verfügbar") {
        maxWidth = Double.MaxValue
        disable = !canBuy
        style =
          if canBuy then
            "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-font-size: 11px;"
          else
            "-fx-background-color: #94a3b8; -fx-text-fill: black; -fx-background-radius: 8; -fx-font-size: 11px;"
        onAction = _ => withState(s => controller.handleInput(BuyCardInput(card.cardName), s))
      }

      marketGrid.children += new VBox(5) {
        prefWidth = 200
        minHeight = 255
        padding = Insets(9)
        alignment = Pos.TopCenter
        style =
          s"""
             |-fx-background-color: ${cardBackground(card.color)};
             |-fx-border-color: ${cardBorder(card.color)};
             |-fx-border-width: 2;
             |-fx-background-radius: 12;
             |-fx-border-radius: 12;
             |-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.16), 6, 0, 0, 2);
             |-fx-text-fill: black;
           """.stripMargin

        children = Seq(
          cardGraphic(card),
          new Label(card.cardName) {
            font = Font.font("Arial", FontWeight.Bold, 14)
            wrapText = true
            alignment = Pos.Center
            maxWidth = 178
            style = "-fx-text-fill: black;"
          },
          new Label(s"Kosten: ${card.price}  |  Würfel: $dice  |  Stapel: ${stack.amount}") {
            font = Font.font("Arial", FontWeight.Bold, 10)
            wrapText = true
            maxWidth = 178
            style = "-fx-text-fill: black;"
          },
          new Label(cardTypeName(card)) {
            style = "-fx-text-fill: black; -fx-font-size: 10px;"
            wrapText = true
            maxWidth = 178
          },
          new Separator(),
          new Label(card.description) {
            wrapText = true
            maxWidth = 178
            font = Font.font(10)
            style = "-fx-text-fill: black;"
          },
          new Region { VBox.setVgrow(this, Priority.Always) },
          buyButton
        )
      }
    }
  }

  private def renderActions(state: Gamestate): Unit = {
    actionBox.children.clear()
    val player = state.CurrentTurnPlayerId + 1

    state.state match {
      case TurnState.StartofTurn =>
        statusLabel.text = s"Spieler $player startet den Zug."

      case TurnState.ChooseDiceAmount =>
        statusLabel.text = "Wähle die Anzahl der Würfel."
        actionBox.children += bigButton("1 Würfel") {
          controller.handleInput(ChooseDiceAmountInput(1), state)
        }
        actionBox.children += bigButton("2 Würfel") {
          controller.handleInput(ChooseDiceAmountInput(2), state)
        }

      case TurnState.Result1 =>
        statusLabel.text = s"Spieler $player hat ${state.DiceResult} gewürfelt."

      case TurnState.AskForRejectionOfResult =>
        statusLabel.text = s"Du hast ${state.DiceResult} gewürfelt. Mit Funkturm neu würfeln?"
        actionBox.children += bigButton("Neu würfeln") {
          controller.handleInput(RejectDiceRollInput(true), state)
        }
        actionBox.children += bigButton("Wurf behalten") {
          controller.handleInput(RejectDiceRollInput(false), state)
        }

      case TurnState.Result2 =>
        statusLabel.text = s"Karteneffekte wurden angewendet. Ergebnis: ${state.DiceResult}."

      case TurnState.Cardeffects =>
        statusLabel.text = "Karteneffekte werden ausgeführt."

      case TurnState.Buyphase =>
        statusLabel.text = "Kaufphase: Karte anklicken oder Zug ohne Kauf beenden."
        actionBox.children += bigButton("Zug beenden / nichts kaufen") {
          endTurnFromGui(state)
        }

      case TurnState.EndofTurn =>
        statusLabel.text = "Zug beendet."

      case TurnState.PlayerWins =>
        statusLabel.text = s"🏆 Spieler $player gewinnt! 🏆"

      case TurnState.ALREADY_OWN_THAT_YELLOW_CARD_WARNING => warning("Du besitzt diese gelbe Karte bereits.")
      case TurnState.ALREADY_OWN_PURPLE_CARD_WARNING     => warning("Du besitzt bereits eine violette Karte.")
      case TurnState.NO_CARDS_LEFT_OF_THAT_TYPE_WARNING  => warning("Von dieser Karte ist keine mehr übrig.")
      case TurnState.YOU_CANT_AFFORD_THIS_WARNING        => warning("Du hast nicht genug Münzen.")
      case TurnState.NONE_EXISTANT_CARDNAME_WARNING      => warning("Diese Karte existiert nicht.")
    }
  }

  private def endTurnFromGui(state: Gamestate): Unit = {
    val nextState = state.iterateTurn().changeState(TurnState.StartofTurn)
    currentState = Some(nextState)
    controller.startTurn(nextState)
  }

  private def warning(text: String): Unit = {
    statusLabel.text = "⚠️ " + text
    actionBox.children += bigButton("Weiter") {
      currentState.foreach { s =>
        val backToBuy = s.changeState(TurnState.Buyphase)
        currentState = Some(backToBuy)
        render(backToBuy)
      }
    }
  }

  private def withState(action: Gamestate => Unit): Unit = currentState.foreach(action)

  private def bigButton(text: String)(action: => Unit): Button =
    new Button(text) {
      font = Font.font("Arial", FontWeight.Bold, 15)
      padding = Insets(10, 22, 10, 22)
      style = "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 10;"
      onAction = _ => action
    }

  private def appendLogFor(state: Gamestate): Unit = {
    val line = state.state match {
      case TurnState.StartofTurn => s"Spieler ${state.CurrentTurnPlayerId + 1} startet den Zug."
      case TurnState.ChooseDiceAmount => "Würfelanzahl wählen."
      case TurnState.Result1 | TurnState.Result2 if state.DiceResult > 0 => s"Wurf: ${state.DiceResult}."
      case TurnState.AskForRejectionOfResult => "Funkturm: Wurf behalten oder neu würfeln."
      case TurnState.Buyphase => s"Spieler ${state.CurrentTurnPlayerId + 1} darf kaufen."
      case TurnState.PlayerWins => s"Spieler ${state.CurrentTurnPlayerId + 1} hat gewonnen."
      case TurnState.ALREADY_OWN_THAT_YELLOW_CARD_WARNING => "Warnung: gelbe Karte bereits vorhanden."
      case TurnState.ALREADY_OWN_PURPLE_CARD_WARNING => "Warnung: violette Karte bereits vorhanden."
      case TurnState.NO_CARDS_LEFT_OF_THAT_TYPE_WARNING => "Warnung: Stapel leer."
      case TurnState.YOU_CANT_AFFORD_THIS_WARNING => "Warnung: zu wenig Münzen."
      case TurnState.NONE_EXISTANT_CARDNAME_WARNING => "Warnung: Kartenname existiert nicht."
      case _ => state.state.toString
    }

    if !logArea.text.value.endsWith(line + "\n") then logArea.appendText(line + "\n")
  }

  private def cardGraphic(card: Card): Pane = {
    imageFor(card) match {
      case Some(img) =>
        new StackPane {
          prefWidth = 160
          prefHeight = 84
          children = Seq(new ImageView(img) {
            fitWidth = 158
            fitHeight = 82
            preserveRatio = true
            smooth = true
          })
        }
      case None =>
        new StackPane {
          prefWidth = 160
          prefHeight = 84
          style =
            s"""
               |-fx-background-color: rgba(255,255,255,0.45);
               |-fx-background-radius: 9;
               |-fx-border-color: ${cardBorder(card.color)};
               |-fx-border-radius: 9;
               |-fx-border-width: 1;
            """.stripMargin
          children = Seq(new Label(card.cardName) {
            textFill = scalafx.scene.paint.Color.Black
            font = Font.font("Arial", FontWeight.Bold, 13)
            wrapText = true
            alignment = Pos.Center
            maxWidth = 144
          })
        }
    }
  }

  private def imageFor(card: Card): Option[Image] = {
    val fileName = imageFileName(card.cardName)
    val candidates = Seq(
      s"/Assets/textures/cards/$fileName",
      s"/textures/cards/$fileName",
      s"/cards/$fileName",
      s"/de/htwg/se/machikoro/remake/view/Gui/Assets.textures.cards/$fileName",
      s"/de/htwg/se/machikoro/remake/view/Gui/Assets/textures/cards/$fileName",
      s"/${card.texturePath}"
    ).distinct

    val fromResources = candidates.view
      .flatMap(path => Option(getClass.getResourceAsStream(path)).map(stream => new Image(stream)))
      .headOption

    fromResources.orElse {
      val fileCandidates = Seq(
        card.texturePath,
        s"src/main/resources/${card.texturePath}",
        s"src/main/resources/Assets/textures/cards/$fileName",
        s"src/main/scala/de/htwg/se/machikoro/remake/view/Gui/Assets.textures.cards/$fileName",
        s"src/main/scala/de/htwg/se/machikoro/remake/view/Gui/Assets/textures/cards/$fileName"
      ).distinct

      fileCandidates.view
        .map(path => new File(path))
        .find(_.exists())
        .map(file => new Image(file.toURI.toString))
    }
  }

  private def imageFileName(cardName: String): String = {
    val n = normalize(cardName)
    n match {
      case name if name.contains("weizenfeld") => "weizenfeld.png"
      case name if name.contains("bauernhof") => "bauernhof.png"
      case name if name.contains("backerei") => "baeckerei.png"
      case name if name.contains("cafe") => "cafe.png"
      case name if name.contains("minimarkt") || name.contains("mini markt") => "minimarkt.png"
      case name if name.contains("wald") => "forrest.png"
      case name if name.contains("buro") => "buero.png"
      case name if name.contains("stadion") => "stadium.png"
      case name if name.contains("fernsehsender") => "fernsehsender.png"
      case name if name.contains("molkerei") => "molkerei.png"
      case name if name.contains("mobel") || name.contains("sagewerk") => "saegewerk.png"
      case name if name.contains("familien") => "familyRestaurant.png"
      case name if name.contains("bergwerk") => "bergwerk.png"
      case name if name.contains("apfel") => "apfelhein.png"
      case name if name.contains("markthalle") => "markthalle.png"
      case name if name.contains("bahnhof") => "bahnhof.png"
      case name if name.contains("einkaufszentrum") => "einkaufszentrum.png"
      case name if name.contains("freizeitpark") => "freizeitpark.png"
      case name if name.contains("funkturm") => "funkturm.png"
      case _ => "weizenfeld.png"
    }
  }

  private def normalize(s: String): String =
    s.toLowerCase
      .replace("ä", "a")
      .replace("ö", "o")
      .replace("ü", "u")
      .replace("ß", "ss")
      .replace("é", "e")
      .replace("-", " ")

  private def cardTypeName(card: Card): String = card.color match {
    case Color.Blue   => "Blau: aktiviert bei jedem passenden Wurf"
    case Color.Green  => "Grün: aktiviert nur beim eigenen Wurf"
    case Color.Red    => "Rot: aktiviert bei Mitspielern"
    case Color.Purple => "Violett: Großprojekt, nur eins erlaubt"
    case Color.Yellow => "Gelb: Wahrzeichen"
  }

  private def cardBackground(color: Color): String = color match {
    case Color.Blue   => "#dbeafe"
    case Color.Green  => "#dcfce7"
    case Color.Red    => "#fee2e2"
    case Color.Purple => "#f3e8ff"
    case Color.Yellow => "#fef9c3"
  }

  private def cardBorder(color: Color): String = color match {
    case Color.Blue   => "#2563eb"
    case Color.Green  => "#16a34a"
    case Color.Red    => "#dc2626"
    case Color.Purple => "#9333ea"
    case Color.Yellow => "#ca8a04"
  }
}
