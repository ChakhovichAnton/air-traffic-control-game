package UserInterface.CustomButton

import java.awt.image.BufferedImage
import scala.swing.*

trait CustomButton(xCoordinate: Int, yCoordinate: Int, width: Int, height: Int):
  val rectangleArcLength    = 30
  private var mouseOnButton = false

  def mouseIsOnButton: Boolean = mouseOnButton

  val buttonCoordinates: Seq[(Int, Int)] = {
    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g             = bufferedImage.createGraphics()

    val redColor   = new Color(255, 0, 0)//colors used for filtering non button pixels
    val greenColor = new Color(0, 255, 0)

    g.setColor(redColor)
    g.fillRect(0, 0, width, height)
    g.setColor(greenColor)
    g.fillRoundRect(0, 0, width, height, rectangleArcLength, rectangleArcLength)

    val coordsSeq = for
      x <- 1 until width
      y <- 1 until height
    yield
      (x, y)
    coordsSeq
      .filter(bufferedImage.getRGB(_, _) == greenColor.getRGB)
      .map((x, y) => (x + xCoordinate, y + yCoordinate))
  }

  def checkIfCoordsAreOnButton(x: Int, y: Int):Boolean =
    if buttonCoordinates.contains((x, y)) then
      mouseOnButton = true
      true
    else
      mouseOnButton = false
      false
  end checkIfCoordsAreOnButton
  
  def buttonClicked() = mouseOnButton = false

  def drawButton(g: Graphics2D): Unit
end CustomButton
