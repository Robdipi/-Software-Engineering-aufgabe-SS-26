package de.htwg.se.machikoro.remake.view.Gui


import de.htwg.se.machikoro.remake.model.turnState.Buyphase
import de.htwg.se.machikoro.remake.controller.main.{BuyCardInput, ChooseDiceAmountInput, ControllerInterface, RejectDiceRollInput, viewObserver}
import de.htwg.se.machikoro.remake.model.{Gamestate, Player, turnState}
import de.htwg.se.machikoro.remake.model.turnState.{Buyphase, Cardeffects}
import scalafx.geometry.Pos
import scalafx.scene.layout.StackPane
import scalafx.Includes.*
import scalafx.geometry.Pos
import scalafx.scene.control.*
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

class GUI(controller: ControllerInterface) extends viewObserver {
  controller.add(this)
  private val rootPane = new StackPane()

  private val boardPane = new BorderPane()

  private val marketGrid = new GridPane {
    hgap = 10
    vgap = 10
  }
  private val marketContainer = new StackPane {
    style =
      """
      -fx-background-color: #3D6B35;
      -fx-background-radius: 20;
      -fx-padding: 20;
      -fx-border-color: #222222;
      -fx-border-width: 3;
      -fx-border-radius: 20;
      """

    children.add(marketGrid)
    marketGrid.alignment = Pos.Center
  }

  private def createPlayerCardStrip(player: Player, vertical: Boolean): Pane = {

    val pane = new Pane()

    val cardWidth = 120 * cardScale
    val cardHeight = 180 * cardScale

    val availableSpace =
      if (vertical) 500.0 else 350.0

    val overlap =
      if (player.properties.size <= 1)
        if (vertical) cardHeight else cardWidth
      else
        math.max(
          10.0,
          (availableSpace -
            (if (vertical) cardHeight else cardWidth)) /
            (player.properties.size - 1)
        )

    player.properties.zipWithIndex.foreach {
      case (card, index) =>

        val image = new Image(
          getClass.getResourceAsStream(
            card.texturePath
          )
        )

        val imageView = new ImageView(image) {
          fitWidth = cardWidth
          preserveRatio = true

          if (vertical)
            layoutY = index * overlap
          else
            layoutX = index * overlap
        }

        pane.children.add(imageView)
    }

    pane
  }

  private val topPlayer = new VBox() {padding = scalafx.geometry.Insets(15)}
  private val bottomPlayer = new VBox() {padding = scalafx.geometry.Insets(15)}
  private val leftPlayer = new VBox() {padding = scalafx.geometry.Insets(15)}
  private val rightPlayer = new VBox() {padding = scalafx.geometry.Insets(15)}
  private val cardScale = 0.7

  private val overlay = new StackPane {
    visible = false
    style =
      """
      -fx-background-color: rgba(0,0,0,0.7);
      """
  }

  private val overlayLabel = new Label {
    textFill = Color.White
    font = Font(24)
  }

  overlay.children.add(overlayLabel)

  boardPane.top = topPlayer
  boardPane.bottom = bottomPlayer
  boardPane.left = leftPlayer
  boardPane.right = rightPlayer
  BorderPane.setAlignment(
    marketContainer,
    Pos.Center
  )

  boardPane.center = marketContainer
  rootPane.children.addAll(boardPane, overlay)

  def root: StackPane = rootPane

  override def update(gamestate: Gamestate): Unit = {
    updatePlayers(gamestate)
    updateMarket(gamestate)
    updateOverlay(gamestate)
  }

  private def updateMarket(gamestate: Gamestate): Unit = {

    marketGrid.children.clear()

    gamestate.cardStacks.zipWithIndex.foreach {
      case (stack, index) =>

        val image = new Image(
          getClass.getResourceAsStream(
            stack.stackCard.texturePath
          )
        )

        val imageView = new ImageView(image) {
          fitWidth = 120 * cardScale
          fitHeight = 180  * cardScale
          preserveRatio = true
        }

        val amountLabel = new Label(
          s"${stack.amount}"
        )

        amountLabel.style =
          """
        -fx-background-color: rgba(0,0,0,0.8);
        -fx-text-fill: white;
        -fx-padding: 5;
        """

        val cardGraphic = new StackPane {
          children = Seq(
            imageView,
            amountLabel
          )
        }

        StackPane.setAlignment(
          amountLabel,
          Pos.BottomRight
        )

        val cardButton = new Button {
          graphic = cardGraphic
        }

        cardButton.onAction = _ => {
          println(stack.stackCard.cardName)
          if(gamestate.state == Buyphase){
            controller.handleInput(BuyCardInput(stack.stackCard.cardName), gamestate)
          }
        }

        marketGrid.add(
          cardButton,
          index % 4,
          index / 4
        )
    }
  }

  private def updatePlayers(gamestate: Gamestate): Unit = {

    topPlayer.children.clear()
    bottomPlayer.children.clear()
    leftPlayer.children.clear()
    rightPlayer.children.clear()

    val currentPlayer =
      gamestate.CurrentTurnPlayerId

    val orderedPlayers =
      rotatePlayers(
        gamestate.Players,
        currentPlayer
      )

    if (orderedPlayers.nonEmpty)
      bottomPlayer.children.add(
        createPlayerPane(orderedPlayers(0),false)
      )

    if (orderedPlayers.size > 1)
      rightPlayer.children.add(
        createPlayerPane(orderedPlayers(1),true)
      )

    if (orderedPlayers.size > 2)
      topPlayer.children.add(
        createPlayerPane(orderedPlayers(2),false)
      )

    if (orderedPlayers.size > 3)
      leftPlayer.children.add(
        createPlayerPane(orderedPlayers(3),true)
      )
  }


  private def createPlayerPane(player: Player, vertical: Boolean): VBox = {
    val pane = new VBox(10)
    pane.alignment = Pos.Center
    pane.children.add(
      new Label(
        s"Player ${player.playerId}"
      )
    )
    pane.children.add(
      new Label(
        s"${player.money} €"
      )
    )
    pane.children.add(createPlayerCardStrip(player,vertical))
    pane
  }
  private def rotatePlayers(players: List[Player], currentPlayerId: Int): List[Player] = {
    val idx = players.indexWhere(_.playerId == currentPlayerId)
    players.drop(idx) ++ players.take(idx)
  }

  private def updateOverlay(gamestate: Gamestate): Unit = {
    gamestate.state match {
      case turnState.ALREADY_OWN_THAT_YELLOW_CARD_WARNING =>
        showOverlay("You already own this yellow card")
      case turnState.ALREADY_OWN_PURPLE_CARD_WARNING =>
        showOverlay("You already own a purple card")
      case turnState.NO_CARDS_LEFT_OF_THAT_TYPE_WARNING =>
        showOverlay("No cards left")
      case turnState.YOU_CANT_AFFORD_THIS_WARNING =>
        showOverlay("Not enough money")

      case turnState.NONE_EXISTANT_CARDNAME_WARNING =>
        showOverlay("Card doesn't exist")

      case turnState.PlayerWins =>
        showOverlay(
          s"Player ${gamestate.CurrentTurnPlayerId + 1} won!"
        )

      case _ =>
        overlay.visible = false
    }
  }

  def showOverlay(text: String): Unit = {
    overlayLabel.text = text
    overlay.visible = true
  }

  def hideOverlay(): Unit = {
    overlay.visible = false
  }
}