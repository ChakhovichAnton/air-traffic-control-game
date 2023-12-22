package UserInterface

import GameLogic.*
import UserInterface.Windows.*
import UserInterface.PopUps.*
import FileHandling.FileHandlingHelperMethods.*
import FileHandling.GameJSONParser.loadGame
import FileHandling.GameReport.writeGameReportFile

import scala.swing.*

object WindowManager:
  //Dimensions of components
  private val leftSideBorderWidth = 10
  private val scrollbarWidth      = 17
  private val leftSidebarWidth    = 174
  private val rightSidebarWidth   = 220
  private val mapWidth            = 600
  private val mapHeight           = 600
  private val frameHeight         = mapHeight + 35//35 is the height of the top
  private val frameWidth          =
    leftSideBorderWidth + 2 * leftSidebarWidth + mapWidth + rightSidebarWidth

  def getDimensions = (
    leftSidebarWidth,
    rightSidebarWidth,
    mapWidth,
    mapHeight,
    frameHeight,
    frameWidth,
    scrollbarWidth,
    leftSideBorderWidth
  )

  //Loading all the game files
  val fileList            = getFileList("GameScenarios", ".json")

  private val gamesAndVisuals = {
    fileList
      .map(loadGame(_, mapWidth, mapHeight))
      .filter(_.isDefined)
      .map(_.get)
      .map(n => (n._1, n._2, n._3, n._4))
  }

  val gameWindows = gamesAndVisuals.map(i => (GameWindow(i._1, i._2, i._3, i._4)))
  gameWindows.foreach(_.closeWindow())

  private var currentlyPausedGame: Option[GameWindow] = None
  private var currentlyActiveGame: Option[GameWindow] = None

  private val pauseWindow       = PauseWindow()
  private val startWindow       = StartWindow()
  private val scoreReportWindow = ScoreReportWindow()
  scoreReportWindow.closeWindow()
  pauseWindow.closeWindow()
  startWindow.openWindow()

  private val windows = new BoxPanel(Orientation.Vertical) {
    contents += startWindow.getContents
    contents += scoreReportWindow
    contents += pauseWindow
    gameWindows.foreach(contents += _.getContent)
  }

  def possiblyStartScenario(scenarioName: String) =
    gameWindows.find(_.game.scenarioName == scenarioName) match
      case Some(scenario) =>
        currentlyActiveGame = Some(scenario)
        startWindow.closeWindow()
        scenario.openWindow()
      case _ =>
        ErrorPopUp(
          "Try another scenario! There is most likely something wrong with the file.",
          500,
          100
        ).getPopUp.open()
  end possiblyStartScenario


  def endScenario() =
    currentlyActiveGame match
      case Some(gW) =>
        gW.closeWindow()
        pauseWindow.closeWindow()
        scoreReportWindow.openWindow()
        scoreReportWindow.setGame(gW.game)

        writeGameReportFile(gW.game.airport.statistics.getScoreReport)

        gW.game.resetGame()
        gW.resetGameWindow()
      case None =>
        ErrorPopUp("Game was not found!", 200, 100).getPopUp.open()

  def openStartWindow() =
    startWindow.openWindow()

  def pauseGame(gW: GameWindow) =
    currentlyPausedGame = Some(gW)
    pauseWindow.openWindow()

  def continueScenario() =
    currentlyPausedGame match
      case Some(gW) =>
        gW.openWindow()
        pauseWindow.closeWindow()
      case None =>
        ErrorPopUp("No active games exist!", 200, 100).getPopUp.open()

  def top = new MainFrame:
    title     = "Air Traffic Control Game"
    contents  = windows
    size      = new Dimension(frameWidth, frameHeight)
    resizable = false
    centerOnScreen()
    iconImage = {
      val source = "Files/airplane_with_background.png"
      readImage(source) match
        case Some(i) => i
        case None    => iconImage
    }
  end top

end WindowManager

