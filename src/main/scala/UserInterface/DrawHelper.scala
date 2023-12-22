package UserInterface

import java.awt.image.BufferedImage
import scala.math.toRadians
import scala.swing.Graphics2D

object DrawHelper:
  def drawRotatedText(g: Graphics2D, text: String, rotationInDeg: Int, x: Int, y: Int) =
    g.translate(x, y)
    g.rotate(toRadians(rotationInDeg))
    g.drawString(text, 0, 0)
    g.rotate(toRadians(-rotationInDeg))
    g.translate(-x, -y)
  end drawRotatedText

  def rotateImage(image: BufferedImage, rotationInRad: Double) =
    val w = image.getWidth
    val h = image.getHeight

    val img = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
    val g   = img.createGraphics()

    g.rotate(rotationInRad, w / 2, h / 2)
    g.drawRenderedImage(image, null)
    g.dispose()
    img
  end rotateImage
end DrawHelper
