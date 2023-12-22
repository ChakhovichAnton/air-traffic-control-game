package UserInterface.GameWindowComponents.SidebarBackground

import FileHandling.FileHandlingHelperMethods.readImage
import UserInterface.Colors.*
import UserInterface.CustomButton.*
import UserInterface.ImageScaler.scaleImage

import scala.swing.*


/**
 * Class with creates a button into the top left corner
 *
 * @param width  describes the whole possible width. Not the actual width of the button
 * @param height describes the whole possible height. Not the actual height of the button  */
class PauseButton(width: Int, height: Int, informationBoxWidth: Int) extends Panel, SidebarBackground:
  this.preferredSize = new Dimension(width, height)
  private val emptyMug = readImage("Files/Empty_coffee_mug.png")
  private val fullMug = readImage("Files/Full_coffee_mug.png")

  val button: CustomButton = {
    if emptyMug.isDefined && fullMug.isDefined then
      val scaledImageHeight = 90 //both images are scaled to the same size
      val scaledEmptyMug    = scaleImage(emptyMug.get, scaledImageHeight, grassColor)
      val scaledFullMug     = scaleImage(fullMug.get, scaledImageHeight, grassColor)

      val x = (width - scaledEmptyMug.getWidth) / 2
      val y = (height - scaledEmptyMug.getHeight) / 2
      ImageButton(scaledEmptyMug, Some(scaledFullMug), x, y, grassColor)
    else
      val buttonHeight = 50
      val buttonWidth = 90
      val buttonX = (width - buttonWidth) / 2
      val buttonY = (height - buttonHeight) / 2
      val buttonLabel = "Pause"
      val buttonTextFont = Font(Font.Dialog, Font.Style.Bold, 20)

      RectangleButton(
        buttonLabel,
        buttonX,
        buttonY,
        buttonWidth,
        buttonHeight,
        yellowGroundMarking,
        buttonTextFont,
        pavementColor,
        Some(pavementColor.brighter()),
        Some(yellowGroundMarking)
      )
  }

  override def paintComponent(g: Graphics2D): Unit =
    super.paintComponent(g)
    fillBackgroundWithGrass(g, width, height)
    drawInformationBoxRounder(g, 0, -5, informationBoxWidth)
    button.drawButton(g)
end PauseButton
