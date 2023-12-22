package UserInterface.Windows

import GameLogic.*
import UserInterface.GameWindowComponents.*

import javax.swing.Timer
import scala.swing.*
import scala.swing.Swing.ActionListener
import scala.swing.event.*

import UserInterface.*

class GameWindow(val game: Game,
                 taxiways: Vector[(Int, Int, Int, Int)],
                 surfaces: Vector[(Int, Int, Int, Int, Color)],
                 images  : Vector[(Int, Int, String)]
                ) extends Panel, Windows.Window():

  private val sidebarTopBorderHeight = 26
  private val airport                = game.airport
  airport.setMapDimensions(mapWidth, mapHeight)

  //Left side
  private val leftSidebar = LeftSidebar(
    leftSidebarWidth,
    mapHeight,
    scrollbarWidth,
    leftSideBorderWidth,
    airport,
    sidebarTopBorderHeight
  )
  private val departuresSidebar = leftSidebar.departuresSidebar
  private val arrivalsSidebar   = leftSidebar.arrivalsSidebar

  //Right Side
  private val rightSidebar = RightSidebar(
    airport,
    rightSidebarWidth,
    mapHeight,
    sidebarTopBorderHeight
  )
  private val (actionButton, pauseButton, textField) = rightSidebar
    .getButtonsAndTextField

  //Center
  private val centerMap = AirportMap(
    mapWidth,
    mapHeight,
    airport,
    taxiways,
    surfaces,
    images
  )
  centerMap.preferredSize = new Dimension(mapWidth, mapHeight)

  private val wholeLayout = new BorderPanel {
    add( leftSidebar.getSidebar, BorderPanel.Position.West  )
    add(              centerMap, BorderPanel.Position.Center)
    add(rightSidebar.getSidebar, BorderPanel.Position.East  )
  }


  def updateGameWindow(errorText :String) =
    departuresSidebar.updateSidebarDepartures(
      airport.statistics.getDepartedPlanes,
      airport.getPlanesReadyAtGates(game.getTime).map(_._2),
      airport.taxiways.getPlanesTaxiingToRunway,
      airport.runways.flatMap(_.takeoffQueue.getPlanesInTheQueue),
      airport.runways.flatMap(_.getPlanesTakeoff)
    )

    arrivalsSidebar.updateSidebarArrivals(
      airport.taxiways.getPlanes.map(_._2),
      airport.air.getPlanes,
      airport.runways.flatMap(_.getPlanesLanding),
      airport.runways.flatMap(_.isWaitingForGate)
    )

    centerMap.repaint()

    rightSidebar.runwayInformation.updateRunways()
    rightSidebar.updatePoints()
    rightSidebar.updateErrorInfo(errorText, game.getTime)
  end updateGameWindow

  def getContent = wholeLayout

  private var errorText = ""

  def sendInput() =
    if textField.text.nonEmpty then
        val (inputIsValid, possibleErrorText) =
          game.inputHandler.reactToInput(textField.text, game.getTime)
        errorText = possibleErrorText
        if inputIsValid then textField.text = ""
  end sendInput


  private var firstTime = true//set to false when the tickers are started for the first time
  private val timer = Timer(1000, ActionListener(e =>
    //semicolons are needed here. Otherwise an error is displayed
    this.game.addSecond();
    centerMap.onSecond()
  ))

  private val ticker = Timer(10, ActionListener(e =>
    if game.airport.seeIfGameEnds && game.getTime > 1 then
      windowManager.endScenario()
    this.game.onTick()
    updateGameWindow(errorText)
    this.errorText = ""
    this.pauseButton.repaint()
  ))

  private val scrollBarTicker = Timer(1, ActionListener(e =>
    //semicolons are needed here. Otherwise an error is displayed
    departuresSidebar.updateScrollBar();
    rightSidebar.runwayInformation.updateScrollBar();
    arrivalsSidebar.updateScrollBar()
  ))

  def openWindow() =
    wholeLayout.visible = true
    textField.requestFocusInWindow()
    if firstTime then
      game.airport.deployAirplanes(0)
      timer.start()
      ticker.start()
      firstTime = false
      scrollBarTicker.start()
      scrollBarTicker.start()
    else
      timer.restart()
      ticker.restart()
      scrollBarTicker.restart()
      scrollBarTicker.restart()
  end openWindow


  def closeWindow() =
    wholeLayout.visible = false
    textField.text = ""
    if timer.isRunning           then timer.stop()
    if ticker.isRunning          then ticker.stop()
    if scrollBarTicker.isRunning then scrollBarTicker.stop()
    if scrollBarTicker.isRunning then scrollBarTicker.stop()
  end closeWindow

  def resetGameWindow() =
    arrivalsSidebar.planeCards.foreach(_.squish())
    departuresSidebar.planeCards.foreach(_.squish())
    centerMap.reset()
    firstTime = true
  end resetGameWindow


  listenTo(actionButton)
  listenTo(textField)
  listenTo(textField.keys)
  listenTo(pauseButton.mouse.clicks)
  listenTo(pauseButton.mouse.moves)
  listenTo(wholeLayout.mouse.wheel)

  reactions += {
    case e: MouseClicked =>
      if pauseButton.button.checkIfCoordsAreOnButton(e.point.x, e.point.y) then
        this.closeWindow()
        windowManager.pauseGame(this)
        pauseButton.button.buttonClicked()
    case e: MouseMoved =>
      pauseButton.button.checkIfCoordsAreOnButton(e.point.x, e.point.y)
    case ButtonClicked(e) => if e == actionButton then sendInput()
    case KeyPressed(_, Key.Enter, _, _) => sendInput()
    case e: MouseWheelMoved =>
      departuresSidebar.moveScrollBar(e.rotation)
      arrivalsSidebar.moveScrollBar(e.rotation)
  }

end GameWindow
