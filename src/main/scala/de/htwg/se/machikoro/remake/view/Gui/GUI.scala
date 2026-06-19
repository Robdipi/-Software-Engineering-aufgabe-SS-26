package de.htwg.se.machikoro.remake.view.Gui

import de.htwg.se.machikoro.remake.controller.main.{
  BuyCard,
  ChooseDiceAmount as ChooseDiceInput,
  ControllerV2,
  RejectDiceRoll,
  viewObserver
}
import de.htwg.se.machikoro.remake.model.{Card, Color, Gamestate, Player, Type, turnState as TurnState}

import scalafx.Includes.*
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.*
import scalafx.scene.text.{Font, FontWeight}

/**
 * Komplette 2D-GUI fuer Machi Koro.
 *
 * Diese Datei ersetzt:
 * src/main/scala/de/htwg/se/machikoro/remake/view/Gui/GUI.scala
 */
class GUI(controller: ControllerV2) extends viewObserver {

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
    padding = Insets(10)
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

  root.padding = Insets(14)
  root.style =
    """
      |-fx-background-color: #fbfaf7;
      |-fx-background-image:
      |  linear-gradient(to right, rgba(15,23,42,0.035) 1px, transparent 1px),
      |  linear-gradient(to bottom, rgba(15,23,42,0.035) 1px, transparent 1px);
      |-fx-background-size: 34px 34px;
    """.stripMargin
  root.top = new VBox(7, titleLabel, new HBox(18, infoLabel, diceLabel) {
    alignment = Pos.Center
  }, statusLabel) {
    alignment = Pos.Center
    padding = Insets(8, 8, 15, 8)
  }
  root.left = playersBox
  root.center = marketScroll
  root.bottom = actionBox

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
          },
          new Label(s"💰 ${player.money} Münzen") {
            font = Font.font("Arial", FontWeight.Bold, 14)
          },
          new Label(landmarkText(player)) {
            wrapText = true
            style = "-fx-text-fill: #475569;"
          },
          new Label(if cardSummary.nonEmpty then cardSummary else "Noch keine Karten") {
            wrapText = true
            maxWidth = 285
            font = Font.font(12)
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
            "-fx-background-color: #1f2937; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-font-size: 11px;"
          else
            "-fx-background-color: #cbd5e1; -fx-text-fill: #111111; -fx-background-radius: 8; -fx-font-size: 11px;"
        onAction = _ => withState(s => controller.handleInput(BuyCard(card.cardName), s))
      }

      marketGrid.children += new VBox(5) {
        prefWidth = 178
        minHeight = 158
        maxHeight = 178
        padding = Insets(8)
        alignment = Pos.TopCenter
        style =
          s"""
             |-fx-background-color: ${cardBackground(card.color)};
             |-fx-border-color: ${cardBorder(card.color)};
             |-fx-border-width: 2;
             |-fx-background-radius: 12;
             |-fx-border-radius: 12;
             |-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.12), 5, 0, 0, 2);
             |-fx-text-fill: #111111;
           """.stripMargin

        children = Seq(
          cardGraphic(card),
          new Label(card.cardName) {
            font = Font.font("Arial", FontWeight.Bold, 13)
            wrapText = true
            alignment = Pos.Center
            maxWidth = 160
            style = "-fx-text-fill: #111111;"
          },
          new Label(s"${card.price} Münzen  ·  Würfel $dice  ·  x${stack.amount}") {
            font = Font.font("Arial", FontWeight.Bold, 10)
            wrapText = true
            style = "-fx-text-fill: #111111;"
          },
          new Label(card.description) {
            wrapText = true
            maxWidth = 160
            font = Font.font(10)
            style = "-fx-text-fill: #111111;"
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
          controller.handleInput(ChooseDiceInput(1), state)
        }
        actionBox.children += bigButton("2 Würfel") {
          controller.handleInput(ChooseDiceInput(2), state)
        }

      case TurnState.Result1 =>
        statusLabel.text = s"Spieler $player hat ${state.DiceResult} gewürfelt."

      case TurnState.AskForRejectionOfResult =>
        statusLabel.text = s"Du hast ${state.DiceResult} gewürfelt. Mit Funkturm neu würfeln?"
        actionBox.children += bigButton("Neu würfeln") {
          controller.handleInput(RejectDiceRoll(true), state)
        }
        actionBox.children += bigButton("Wurf behalten") {
          controller.handleInput(RejectDiceRoll(false), state)
        }

      case TurnState.Result2 =>
        statusLabel.text = s"Karteneffekte wurden angewendet. Ergebnis: ${state.DiceResult}."

      case TurnState.Cardeffects =>
        statusLabel.text = "Karteneffekte werden ausgeführt."

      case TurnState.Buyphase =>
        statusLabel.text = "Kaufphase: Karte anklicken oder Zug ohne Kauf beenden."
        actionBox.children += bigButton("Zug beenden / nichts kaufen") {
          controller.handleInput(BuyCard("next"), state)
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
          prefWidth = 150
          prefHeight = 68
          children = Seq(new ImageView(img) {
            fitWidth = 148
            fitHeight = 66
            preserveRatio = true
            smooth = true
          })
        }
      case None =>
        new StackPane {
          prefWidth = 150
          prefHeight = 68
          style = s"-fx-background-color: ${cardBackground(card.color)}; -fx-background-radius: 10; -fx-border-color: ${cardBorder(card.color)}; -fx-border-radius: 10;"
          children = Seq(new Label(card.cardName) {
            textFill = scalafx.scene.paint.Color.Black
            font = Font.font("Arial", FontWeight.Bold, 12)
            wrapText = true
            alignment = Pos.Center
            maxWidth = 136
          })
        }
    }
  }

  private def imageFor(card: Card): Option[Image] = {
    val fileName = imageFileName(card.cardName)
    val candidates = Seq(
      s"/de/htwg/se/machikoro/remake/view/Gui/Assets.textures.cards/$fileName",
      s"/de/htwg/se/machikoro/remake/view/Gui/Assets/textures/cards/$fileName",
      s"/${card.texturePath}"
    ).distinct

    candidates.view
      .flatMap(path => Option(getClass.getResourceAsStream(path)).map(stream => new Image(stream)))
      .headOption
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
    case Color.Blue   => "#edf5ff"
    case Color.Green  => "#eefaf0"
    case Color.Red    => "#fff0f0"
    case Color.Purple => "#f7f0ff"
    case Color.Yellow => "#fff9dc"
  }

  private def cardBorder(color: Color): String = color match {
    case Color.Blue   => "#2563eb"
    case Color.Green  => "#16a34a"
    case Color.Red    => "#dc2626"
    case Color.Purple => "#9333ea"
    case Color.Yellow => "#ca8a04"
  }
}
