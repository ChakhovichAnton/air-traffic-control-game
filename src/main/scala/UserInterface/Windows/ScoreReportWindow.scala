package UserInterface.Windows

import GameLogic.*
import UserInterface.*
import UserInterface.Colors.*
import UserInterface.CustomButton.RectangleButton

import javax.swing.Timer
import scala.swing.*
import scala.swing.Swing.*
import scala.swing.event.*

class ScoreReportWindow() extends Panel, Windows.Window():
  //Colors:
  private val backgroundColor    = skyColor//in case background image is not loaded
  private val headerColor        = whiteColor
  private val labelTextColor     = whiteColor
  private val boxBackgroundColor = pavementColor
  private val insideBoxColor     = new Color(105, 157, 224, 80)
  //insideBoxColor is the color of the smaller boxes with numbers. 80 is the transparency

  private val buttonTextColor = whiteColor
  private val buttonMColor    = pavementColor.brighter()//button color when mouse is on top
  private val buttonNColor    = pavementColor//button color when mouse is not on top

  //Fonts:
  private val headerFont     = Font(Font.Dialog, Font.Style.Bold, 50)
  private val normalTextFont = Font(Font.Dialog, Font.Style.Bold, 30)
  private val boxTextFont    = Font(Font.Dialog, Font.Style.Bold, 17)

  //Main components of the window. I've currently inserted some example values
  //but most of these will be updated when the screen is loaded.
  private var headerString    = "You won!" //"You won!" or "You lost!"
  private val arrivalsText    = "Arrivals Score"
  private val departuresText  = "Departures Score"
  private val totalText       = "Total Score"
  private var arrivalsScore   = "000 000" //max length is 7 digits. 6 digits and a space
  private var departuresScore = "000 000"
  private var totalScore      = "000 000"
  private var scenarioName    = "Test"

  //Left side statistics box. "ls" stands for "left side"
  private val lsBoxBorderWidth = 10
  private val lsSmallBoxHeight = 50
  private val lsSmallBoxWidth  = 70
  private val lsSmallBoxCount  = 4

  private val lsBoxX      = 60//top left x coordinate
  private val lsBoxY      = 200 - lsBoxBorderWidth//top left y coordinate
  private val lsBoxWidth  = 180
  private val lsBoxHeight =
    (1 + lsSmallBoxCount) * lsBoxBorderWidth + lsSmallBoxCount * lsSmallBoxHeight

  private var boxLandedCount    = "0"
  private var boxTakeoffCount   = "0"
  private var boxCrashCount     = "0"
  private var boxDeadPassangers = "0"

  //Button
  private val buttonWidth  = 200
  private val buttonHeight = 60
  private val buttonX      = (frameWidth - buttonWidth)/2 - 8 //-8 is for correcting
  private val buttonY      = 470
  private val buttonText   = "Game Menu"

  private val button = RectangleButton(
    buttonText,
    buttonX,
    buttonY,
    buttonWidth,
    buttonHeight,
    buttonTextColor,
    normalTextFont,
    buttonNColor,
    Some(buttonMColor),
    Some(buttonTextColor)//same color as button text: matching colors makes it look better
  )

  def setGame(game: Game) =
    //spacesIntoPoints-method adds spaces into points
    //For example it turns 321000 into 321 000. Turns 3120000 into 3 120k
    def spacesIntoPoints(points: String) = {
      var count = 0
      var newPoints = ""
      for p <- points.reverse do
        if count != 0 && count%3 == 0 then
          newPoints += " "
        newPoints += p
        count += 1
      if newPoints.length > 10 then
        newPoints = "k999 99" //can only handle under 99 999 000 points. This is a reversed value of it
      else if newPoints.length > 7 then
        newPoints = newPoints.drop(4)
        newPoints = 'k' +: newPoints
      newPoints.reverse
    }

    val stats = game.airport.statistics
    headerString    = if stats.noCrash then "You won!" else "You lost!"
    arrivalsScore   = spacesIntoPoints(stats.arrivalsPoints.toString)
    departuresScore = spacesIntoPoints(stats.departuresPoints.toString)
    totalScore      = spacesIntoPoints(stats.totalPoints.toString)
    scenarioName    = game.scenarioName

    boxLandedCount    = stats.arrivalsPlaneCount.toString
    boxTakeoffCount   = stats.departuresPlaneCount.toString
    boxCrashCount     = stats.crashedPlaneCount.toString
    boxDeadPassangers = stats.casualtyCount.toString
  end setGame


  override def paintComponent(g : Graphics2D) =
    super.paintComponent(g)

    /**
     * Method returns the x coordinate of a centered text
     * @param font font of the text
     * @param text the text
     * @return the x coordinate as an Int
     */
    def getCenterTextXCoord(font: Font, text: String):Int =
      val bounds = font.getStringBounds(text, g.getFontRenderContext).getBounds
      (frameWidth - bounds.width)/2 - 10//-10 is used for correcting
    end getCenterTextXCoord

    //Background image
    val backgroundImgSource = "Files/background_airport_blurred.png"
    FileHandling.FileHandlingHelperMethods.readImage(backgroundImgSource) match
      case Some(i) =>
        g.drawImage(i, 0, 0, null)
      case None    =>
        g.setColor(backgroundColor)
        g.fillRect(0, 0, frameWidth, frameHeight)

    //Drawing the header and scenario text background
    val scenarioText      = "Scenario: " + scenarioName
    val scenarioTextWidth = normalTextFont
      .getStringBounds(scenarioText, g.getFontRenderContext)
      .getBounds.width

    val headerY               = 100
    val scenarioNameY         = 150
    val backgroundExtraWidth  = 15

    val backgroundX      = getCenterTextXCoord(normalTextFont, scenarioText) - backgroundExtraWidth
    val backgroundY      = headerY - headerFont.getSize
    val backgroundWidth  = scenarioTextWidth + 2 * backgroundExtraWidth
    val backgroundHeight = 120

    g.setColor(pavementColor)
    g.fillRoundRect(
      backgroundX,
      backgroundY,
      backgroundWidth,
      backgroundHeight,
      30,//the 30s are the curve angles
      30
    )

    //Header Text
    g.setFont(headerFont)
    g.setColor(headerColor)
    g.drawString(headerString,
                 getCenterTextXCoord(headerFont, headerString),
                 headerY
                )

    //Drawing the scenario name
    g.setFont(normalTextFont)
    g.drawString(scenarioText,
                 getCenterTextXCoord(normalTextFont, scenarioText),
                 scenarioNameY
                )

    //Score labels values
    val normalTextX = 350
    val pointsX     = normalTextX + 300
    val firstY      = 230
    val YDifference = 80
    val scoreLabelBackgroundBorder = 10
    val pointsTextMaxBounds = normalTextFont
      .getStringBounds("000 000", g.getFontRenderContext)//Max length is 7
      .getBounds

    //Drawing the score labels background
    val pointsBorderWidth = 5
    g.setColor(boxBackgroundColor)
    g.fillRect(
      normalTextX - scoreLabelBackgroundBorder,
      firstY - normalTextFont.getSize - scoreLabelBackgroundBorder,
      pointsX - normalTextX + pointsTextMaxBounds.width + pointsBorderWidth + 2 * scoreLabelBackgroundBorder,
      3 * YDifference - 2 * scoreLabelBackgroundBorder
    )

    //Drawing the score labels
    g.setColor(labelTextColor)
    g.drawString(arrivalsText   , normalTextX, firstY                  )
    g.drawString(departuresText , normalTextX, firstY + YDifference    )
    g.drawString(totalText      , normalTextX, firstY + 2 * YDifference)

    //Drawing the points. x and y are the coordinates of the text and the rectangle is drawn around it.
    def drawPointsRectangle(x: Int, y: Int, points: String) =
      g.setColor(insideBoxColor)
      g.fillRect(
        x - pointsBorderWidth,
        y - normalTextFont.getSize - 4,
        pointsTextMaxBounds.width + 2 * pointsBorderWidth,
        pointsTextMaxBounds.height + 8
      )
      g.setColor(labelTextColor)
      g.drawString(points, x, y)
    end drawPointsRectangle

    drawPointsRectangle(pointsX, firstY                  , arrivalsScore  )
    drawPointsRectangle(pointsX, firstY + YDifference    , departuresScore)
    drawPointsRectangle(pointsX, firstY + 2 * YDifference, totalScore     )
    button.drawButton(g)

    //Drawing the statistics boxes
    //Method draws a rectangle with text in the center of it. Method assumes that the text fits in the rectangle
    def drawRectangleWithCount(x: Int, y: Int, width: Int, height: Int, count: String) =
      val font = boxTextFont
      val bounds = font.getStringBounds(count, g.getFontRenderContext).getBounds
      val textX = x + (width - bounds.width)/2
      val textY = y + font.getSize + (height - font.getSize)/2 - 2 //-2 is used for correcting

      g.setColor(insideBoxColor)
      g.fillRect(x, y, width, height)
      g.setColor(labelTextColor)
      g.setFont(font)
      g.drawString(count, textX, textY)
    end drawRectangleWithCount


    val boxTextX      = lsBoxX + lsBoxWidth - lsSmallBoxWidth - lsBoxBorderWidth//The top left x coordinate of the small text boxes
    val boxFirstY     = lsBoxY + lsBoxBorderWidth//The top left x coordinate of the first small box

    val boxLabelX  = lsBoxX + lsBoxBorderWidth
    val boxLabelY1 = 21//Correction for the top box label
    val boxLabelY2 = 35//Correction for the bottom box label
    val boxLabelC  = 28//Correction if the label is in the middle

    g.setColor(boxBackgroundColor)
    g.fillRect(lsBoxX, lsBoxY, lsBoxWidth, lsBoxHeight)

    g.setColor(labelTextColor)
    g.setFont(boxTextFont)
    g.drawString("Arrived", boxLabelX, boxFirstY + boxLabelY1)
    g.drawString("Airplanes", boxLabelX, boxFirstY + boxLabelY2)
    drawRectangleWithCount(//Landings
      boxTextX,
      boxFirstY,
      lsSmallBoxWidth,
      lsSmallBoxHeight,
      boxLandedCount
    )
    g.drawString("Departed", boxLabelX, boxFirstY + boxLabelY1 + lsSmallBoxHeight + lsBoxBorderWidth)
    g.drawString("Airplanes", boxLabelX, boxFirstY + boxLabelY2 + lsSmallBoxHeight + lsBoxBorderWidth)
    drawRectangleWithCount(//Takeoffs
      boxTextX,
      boxFirstY + lsSmallBoxHeight + lsBoxBorderWidth,
      lsSmallBoxWidth,
      lsSmallBoxHeight,
      boxTakeoffCount
    )
    g.drawString("Crashed", boxLabelX, boxFirstY + boxLabelY1 + 2 * (lsSmallBoxHeight + lsBoxBorderWidth))
    g.drawString("Airplanes", boxLabelX, boxFirstY + boxLabelY2 + 2 * (lsSmallBoxHeight + lsBoxBorderWidth))
    drawRectangleWithCount(//Crashes
      boxTextX,
      boxFirstY + 2 * (lsSmallBoxHeight + lsBoxBorderWidth),
      lsSmallBoxWidth,
      lsSmallBoxHeight,
      boxCrashCount
    )
    g.drawString("Casualties", boxLabelX, boxFirstY + boxLabelC + 3 * (lsSmallBoxHeight + lsBoxBorderWidth))
    drawRectangleWithCount(//Dead passangers
      boxTextX,
      boxFirstY + 3 * (lsSmallBoxHeight + lsBoxBorderWidth),
      lsSmallBoxWidth,
      lsSmallBoxHeight,
      boxDeadPassangers
    )
  end paintComponent

  private val ticker = Timer(1, ActionListener(e =>
    this.repaint()
  ))
  
  def openWindow() =
    this.visible = true
    ticker.start()
    
  def closeWindow() =
    this.visible = false
    ticker.stop()

  listenTo(mouse.clicks)
  listenTo(mouse.moves)

  reactions += {
    case e: MouseClicked =>
      if button.checkIfCoordsAreOnButton(e.point.x, e.point.y) then
        closeWindow()
        windowManager.openStartWindow()
    case e: MouseMoved =>
      button.checkIfCoordsAreOnButton(e.point.x, e.point.y)
  }
end ScoreReportWindow
