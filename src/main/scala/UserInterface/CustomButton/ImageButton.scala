package UserInterface.CustomButton

import UserInterface.ImageScaler
import scala.swing.*
import java.awt.image.BufferedImage

/**
 * Class for making a button. Functionality of the button needs to be
 * implemented separately. This class assumes that bufferedImage and
 * bufferedImageWithMouse are similar, with their dimensions not
 * changing. Colors can change. The images can't have the same color
 * as the background.
 */
class ImageButton(bufferedImage: BufferedImage,
                  bufferedImageWithMouse: Option[BufferedImage],
                  xCoordinate: Int,
                  yCoordinate: Int,
                  backgroundColor: Color
                 ) extends CustomButton(xCoordinate,
                                        yCoordinate,
                                        bufferedImage.getWidth,
                                        bufferedImage.getHeight
                                      ):

  override val buttonCoordinates: Seq[(Int, Int)] = {
    val coordsSeq = for
      x <- 1 until bufferedImage.getWidth
      y <- 1 until bufferedImage.getHeight
    yield
      (x,y)

    coordsSeq
      .filterNot(bufferedImage.getRGB(_, _) == backgroundColor.getRGB)
      .map((x, y) => (x + xCoordinate, y + yCoordinate))
  }

  def drawButton(g: Graphics2D): Unit =
    val image = bufferedImageWithMouse match
      case Some(img) if mouseIsOnButton => img
      case _ => bufferedImage
    g.drawImage(image, xCoordinate, yCoordinate, null)
end ImageButton
