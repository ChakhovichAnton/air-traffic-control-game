package UserInterface.Windows

import FileHandling.FileHandlingHelperMethods.readImage
import UserInterface.*
import UserInterface.Colors.*

import java.awt.image.BufferedImage
import scala.swing.*
import scala.swing.event.*

class PauseWindow() extends Panel, Windows.Window:
  private val headerFont     = Font(Font.Dialog, Font.Style.Bold, 50)
  private val buttonTextFont = Font(Font.Dialog, Font.Style.Bold, 40)

  //Button box
  private val buttonBoxWidth  = 600
  private val buttonBoxHeight = 350
  private val buttonBoxX = (frameWidth - buttonBoxWidth)/2 - 5//-5 is for correcting
  private val buttonBoxY = (frameHeight - buttonBoxHeight)/2

  //Buttons
  private val buttonWidth   = 200
  private val buttonHeight  = 75
  private val buttonY = buttonBoxY + (buttonBoxHeight - buttonHeight)/2

  private val resumeButtonX = buttonBoxX + (buttonBoxWidth/2 - buttonWidth)/2
  private val resumeButton = CustomButton.RectangleButton(
      "RESUME",
      resumeButtonX,
      buttonY,
      buttonWidth,
      buttonHeight,
      yellowGroundMarking,
      buttonTextFont,
      pavementColor,
      Some(pavementColor.brighter()),
      Some(yellowGroundMarking)
    )
  private val exitButtonX = buttonBoxX + buttonBoxWidth/2 + (buttonBoxWidth/2 - buttonWidth)/2
  private val exitButton = CustomButton.RectangleButton(
      "EXIT",
      exitButtonX,
      buttonY,
      buttonWidth,
      buttonHeight,
      yellowGroundMarking,
      buttonTextFont,
      pavementColor,
      Some(pavementColor.brighter()),
      Some(yellowGroundMarking)
  )

  override def paintComponent(g: Graphics2D): Unit =
    super.paintComponent(g)
    //Background
    val backgroundImgSource = "Files/background_airport_blurred.png"
    FileHandling.FileHandlingHelperMethods.readImage(backgroundImgSource) match
      case Some(i) =>
        g.drawImage(i, 0, 0, null)
        g.setColor(pavementColor.brighter().brighter())
      case None    =>
        g.setColor(skyColor)
        g.fillRect(0, 0, frameWidth, frameHeight)
        g.setColor(grassColor)
    end match

    //The box with the buttons. The color is determined above, depending on the background image
    g.fillRoundRect(buttonBoxX, buttonBoxY, buttonBoxWidth, buttonBoxHeight, 30, 30)

    //Header box and header
    val headerText = "PAUSED"
    val headerTextBounds = headerFont.getStringBounds(headerText, g.getFontRenderContext).getBounds
    val headerX    = (frameWidth - headerTextBounds.width)/2 - 5//-5 is for correcting
    val headerY    = buttonBoxY + 10

    val fileSource = "Files/airplane_pause_menu.png"
    readImage(fileSource) match
      case Some(img) =>
        val scaledImageHeight = 600
        val scaledImage = ImageScaler
          .scaleImage(img, scaledImageHeight, new Color(0, 0, 0, 0))//transparent color
        val imgX = (frameWidth - scaledImage.getWidth)/2 + 8
        //8 adjusts the plane slightly to the right for visual reasons
        val imgY = buttonBoxY - scaledImage.getHeight/2
        g.drawImage(scaledImage, imgX, imgY, null)
      case None =>//"br" stands for backgroundRectangle
        val sideBorder         = 50
        val topAndBottomBorder = 20
        val brWidth = headerTextBounds.width + 2 * sideBorder
        val brHeight = headerTextBounds.height + 2 * topAndBottomBorder
        val brX = headerX - sideBorder
        val brY = headerY - headerTextBounds.height
        g.setColor(whiteColor)
        g.fillRoundRect(brX, brY, brWidth, brHeight, 30, 30)

    g.setColor(pavementColor)
    g.setFont(headerFont)
    g.drawString(headerText, headerX, headerY)

    resumeButton.drawButton(g)
    exitButton.drawButton(g)
  end paintComponent

  def closeWindow() =
    this.visible = false

  def openWindow() =
    this.visible = true

  listenTo(mouse.moves)
  listenTo(mouse.clicks)

  reactions += {
    case e: MouseClicked =>
      if exitButton.checkIfCoordsAreOnButton(e.point.x, e.point.y) then
        windowManager.endScenario()
      if resumeButton.checkIfCoordsAreOnButton(e.point.x, e.point.y) then
        windowManager.continueScenario()
      this.repaint()
    case e: MouseMoved =>
      exitButton.checkIfCoordsAreOnButton(e.point.x, e.point.y)
      resumeButton.checkIfCoordsAreOnButton(e.point.x, e.point.y)
      this.repaint()
  }
end PauseWindow
