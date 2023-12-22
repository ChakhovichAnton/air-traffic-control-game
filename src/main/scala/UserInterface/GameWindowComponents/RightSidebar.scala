package UserInterface.GameWindowComponents

import FileHandling.FileHandlingHelperMethods.readImage
import GameLogic.*
import UserInterface.CustomButton.*
import UserInterface.ImageScaler.scaleImage
import UserInterface.Colors.*
import UserInterface.GameWindowComponents.SidebarBackground.*

import java.awt.image.BufferedImage
import javax.swing.BorderFactory.createMatteBorder
import scala.swing.BorderPanel.Position
import scala.swing.*

class RightSidebar(airport: Airport,
                   width: Int,
                   height: Int,
                   topBorderHeight: Int
                  ):

  private val sidebarDimensions  = new Dimension(width, height)
  private var errorDisapearsTime = 0

  private val input       = new TextField()
  private val sendButton  = new Button("Send Instructions To Aircraft")
  private val information = new Label("Instructions")
  private val pointsInfo  = new Label("Current Points")
  private val points      = new Label("0")

  private val topErrorInfo    = new Label("")
  private val bottomErrorInfo = new Label("")

  points.font                 = Font(Font.Dialog, Font.Style.Bold, 30)
  pointsInfo.xLayoutAlignment = 0.5
  information.border          = createMatteBorder(1, 0, 0, 0, blackColor)
  bottomErrorInfo.border      = createMatteBorder(0, 0, 1, 0, blackColor)

  private val rightSideWidth = 10
  private val infoBoxWidth   = width - rightSideWidth

  private val runwayCards        = airport.runways.map(RunwayCard(_, airport, width))
  val runwayInformation          = RunwayInfo(runwayCards)//accessed from outside the class to update the runways
  private val RunwayInfoWindow   = runwayInformation.getRunwayWindow
  RunwayInfoWindow.preferredSize = new Dimension(infoBoxWidth, height)


  private val pauseButtonHeight      = 130
  private val topHeightUntillInfoBox = 5

  private val sidebarRightSide = GrassBackground(rightSideWidth, height)
  private val pauseButton      = PauseButton(width, pauseButtonHeight, infoBoxWidth)
  private val sidebarTop       =
    SidebarEndBackground(width, infoBoxWidth, topBorderHeight, 0, topBorderHeight - topHeightUntillInfoBox)

  //Defining some basic instructions
  private val inst1 = new Label("Takeoff:")
  private val inst2 = new Label("T<Runway Name>")
  private val inst3 = new Label("Landing:")
  private val inst4 = new Label("<Flight Number>L<Runway Name>")
  private val inst5 = new Label("Taxiing:")
  private val inst6 = new Label("<Flight Number>T<Runway or Gate name>")

  private val instFont = Font(Font.Dialog, Font.Style.Bold, 9)

  def getSidebar =
    val instuctionsPanel = new BoxPanel(Orientation.Vertical) {
      contents += inst1
      contents += inst2
      contents += inst3
      contents += inst4
      contents += inst5
      contents += inst6
    }
    instuctionsPanel.contents.foreach(_.font = instFont)

    val sidebarInstructions = new BorderPanel {
      add(     information, Position.North )
      add(instuctionsPanel, Position.Center)
    }

    val errorInfo = new BorderPanel {
      add(topErrorInfo, BorderPanel.Position.Center)
      add(bottomErrorInfo, BorderPanel.Position.South)
    }

    val sideBarInput = new BorderPanel {
      add(input     , Position.North )
      add(sendButton, Position.Center)
      add(errorInfo , Position.South )
    }

    val sidebarInformation = new BoxPanel(Orientation.Vertical) {
      contents += new BorderPanel {
        add(pointsInfo, Position.North )
        add(points    , Position.Center)
      }
      contents += sidebarInstructions
      contents += sideBarInput
      contents += RunwayInfoWindow
    }

    val wholeSidebar = new BorderPanel {
      add(        sidebarTop, Position.North )
      add(sidebarInformation, Position.Center)
      add(  sidebarRightSide, Position.East  )
      add(       pauseButton, Position.South )
    }

    wholeSidebar.minimumSize   = sidebarDimensions
    wholeSidebar.preferredSize = sidebarDimensions
    wholeSidebar.maximumSize   = sidebarDimensions

    wholeSidebar
  end getSidebar


  def getButtonsAndTextField = (sendButton, pauseButton, input)

  /**
   * Method returns the points formatted into a nine digits.
   * For example turns "5678" to "000 005 678"
   */
  private def formatPoints(points: String) =
    val pointsPrefixByLengthMap =Map(
      0 -> "000 000 000",
      1 -> "000 000 00",
      2 -> "000 000 0",
      3 -> "000 000 ",
      4 -> "000 00",
      5 -> "000 0",
      6 -> "000 ",
      7 -> "00",
      8 -> "0",
      9 -> ""
    )

    val l = points.length
    if l > 9 then
      "999 999 999"
    else
      var count = 0
      var newPoints = ""
      for p <- points.reverse do
        if count != 0 && count%3 == 0 then
          newPoints += " "//Adding the spaces into the points
        newPoints += p
        count += 1
      end for

      pointsPrefixByLengthMap(l) + newPoints.reverse
    end if
  end formatPoints

  def updatePoints() =
    points.text = formatPoints(airport.statistics.totalPoints.toString)
    points.repaint()
    pauseButton.repaint()


  private val maxTextLengthOnLine = 38

  /**
   * Method can format error info only on two 38 char long lines.
   * Otherwise rest of the message, isn't visible
   */
  def updateErrorInfo(text: String, currentTimeInSeconds: Int) =
    def onFirstLabel() = {
      topErrorInfo.text       = text
      errorDisapearsTime      = currentTimeInSeconds + 10
      topErrorInfo.visible    = true
      bottomErrorInfo.visible = false
    }

    if currentTimeInSeconds == errorDisapearsTime then
      topErrorInfo.visible    = false
      bottomErrorInfo.visible = false
      topErrorInfo.text       = ""
      bottomErrorInfo.text    = ""
    if text.length <= maxTextLengthOnLine && text != "" then
      onFirstLabel()
    else if text.length > maxTextLengthOnLine then//formatting text on the two rows
      val i = text.take(maxTextLengthOnLine).lastIndexWhere(_ == ' ')
      if i == -1 then
        onFirstLabel()
      else
        topErrorInfo.text       = text.take(i + 1)
        bottomErrorInfo.text    = text.drop(i + 1)
        topErrorInfo.visible    = true
        bottomErrorInfo.visible = true
    end if

    topErrorInfo.repaint()
    bottomErrorInfo.repaint()
end RightSidebar
