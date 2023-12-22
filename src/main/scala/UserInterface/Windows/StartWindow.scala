package UserInterface.Windows

import GameLogic.*
import UserInterface.PopUps.*
import UserInterface.Windows
import UserInterface.Colors.*

import javax.swing.Timer
import scala.swing.*
import scala.swing.Swing.*
import scala.swing.event.ButtonClicked

class StartWindow() extends Panel, Windows.Window():
  //Building the start window layout
  private val gameName           = new Label("Air Trafic Control Game")
  private val instructionsButton = new Button("Instructions")
  private val gameReportButton   = new Button("Game Reports")

  gameName.font                  = Font(Font.Dialog, Font.Style.Bold, 40)
  private val buttonWidth        = 200
  private val buttonHeight       = 50
  private val buttonDimensions   = new Dimension(buttonWidth, buttonHeight)
  private val backgroundColor    = skyColor

  private val startMenu = new MenuBar()
  private val menu      = new Menu("Choose scenario!")
  menu.font             = Font(Font.Dialog, Font.Style.Bold, 30)

  private val menuSize  = menu.preferredSize
  startMenu.maximumSize = menu.preferredSize
  menu.preferredSize    = menuSize

  private def getScenarioName(path: String) = {
    path
      .takeRight(path.length - path.lastIndexWhere(_.toString == """\""") - 1)
      .takeWhile(_ != '.')
      .capitalize
  }

  private val menuItems = {
    windowManager
      .fileList
      .map(getScenarioName(_))
      .map(new MenuItem(_))
  }

  private val menuItemHeight = menuItems.headOption match
    case Some(i) => i.preferredSize.height
    case _ => 21 //21 is item standard height
  menuItems.foreach(_.preferredSize = new Dimension(menuSize.width - 3, menuItemHeight))//-3 is needed for correcting the item width

  menuItems.foreach(menu.contents += _)
  startMenu.contents += menu

  
  private val wholeLayout = new BoxPanel(Orientation.Vertical) {
    contents += VStrut(30)
    contents += new BorderPanel {
      add(gameName, BorderPanel.Position.North)
      background = backgroundColor
    }
    contents += VStrut(50)
    contents += startMenu
    contents += VStrut(30)
    contents += new BorderPanel {
      add(new BoxPanel(Orientation.Horizontal){
        contents += HStrut((frameWidth - buttonWidth)/2 - 10)//-10 is needed for correcting the width
        contents += instructionsButton
        background = backgroundColor
      }, BorderPanel.Position.South)
      background = backgroundColor
    }
    contents += VStrut(50)
    contents += new BorderPanel {
      add(new BoxPanel(Orientation.Horizontal){
        contents += HStrut((frameWidth - buttonWidth)/2 - 10)//-10 is needed for correcting the width
        contents += gameReportButton
        background = backgroundColor
      }, BorderPanel.Position.South)
      background = backgroundColor
    }
    contents += VStrut(150)
    background = backgroundColor
  }

  instructionsButton.maximumSize   = buttonDimensions
  instructionsButton.preferredSize = buttonDimensions

  gameReportButton.maximumSize     = buttonDimensions
  gameReportButton.preferredSize   = buttonDimensions

  private val gameReportPopUpW  = 500//width
  private val gameReportPopUpH  = 300//height
  private val instructionsPopUp = InstructionsPopUp(500, 350)
  private var gameReportPopUp   = GameReportPopUp(gameReportPopUpW, gameReportPopUpH)
  //a new GameReportPopUp is loaded every single time it is opened, hence it is a "var"

  private var firstTime = true
  private val gameReportScrollBarTicker =
    Timer(1, ActionListener(e => gameReportPopUp.updateScrollBar()))

  def openWindow() =
    wholeLayout.visible = true
    if firstTime then
      gameReportScrollBarTicker.start()
      firstTime = false
    else
      gameReportScrollBarTicker.restart()
  end openWindow

  def closeWindow() =
    wholeLayout.visible = false
    instructionsPopUp.getPopUp.close()
    gameReportPopUp.getPopUp.close()
    
    if gameReportScrollBarTicker.isRunning then
      gameReportScrollBarTicker.stop()
  end closeWindow

  def getContents = wholeLayout

  listenTo(instructionsButton)
  listenTo(gameReportButton)
  menuItems.foreach(listenTo(_))

  reactions += {
    case ButtonClicked(e) if e == instructionsButton =>
      instructionsPopUp.openPopUp()
    case ButtonClicked(e) if e == gameReportButton =>
      //creating a new object to load the new game reports
      gameReportPopUp = GameReportPopUp(gameReportPopUpW, gameReportPopUpH)
      gameReportPopUp.openPopUp()
    case ButtonClicked(e) =>
      windowManager.possiblyStartScenario(e.text)
  }
end StartWindow
