package UserInterface

import java.awt.image.BufferedImage
import scala.swing.Color

object ImageScaler:
  def scaleImage(image: BufferedImage, height: Int, backgroundColor: Color):BufferedImage =
    val scaledWidth  = image.getWidth * height / image.getHeight
    val scaledImg  = image.getScaledInstance(scaledWidth, height, 0)

    //Turning the scaled image into a new buffered image
    val bufferedImage = BufferedImage(scaledWidth, height, BufferedImage.TYPE_INT_ARGB)
    val g             = bufferedImage.createGraphics()

    g.setColor(backgroundColor)
    g.fillRect(0, 0, scaledWidth, height)
    g.drawImage(scaledImg, 0, 0, null)

    bufferedImage
  end scaleImage

end ImageScaler
