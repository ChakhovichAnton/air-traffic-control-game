package UserInterface.GameWindowComponents

import UserInterface.Colors.*

import scala.swing.*
import scala.math.pow
import java.awt.image.BufferedImage

object TaxiwayCurves:
  val curveImageWidth      = 16
  private val markingRGB   = yellowGroundMarking.getRGB
  private val pavementRGB  = pavementColor.getRGB
  private val curve6Radius =
    Vector((1, 6), (2, 6), (3, 5), (4, 5), (5, 4), (5, 3), (6, 2), (6, 1), (0, 6), (6, 0))

  private def getSquarePixels(sideWidth: Int): Vector[(Int, Int)] = {
    for
      x <- (0 until sideWidth).toVector
      y <-  0 until sideWidth
    yield
      (x, y)
  }

  private def setPixelsColor(image: BufferedImage,
                             pixels: IndexedSeq[(Int, Int)],
                             colorRGB: Int
                            ) = {
    pixels.foreach((x, y) => image.setRGB(x, y, colorRGB))
  }

  private def isInTheBigCircle(x: Int, y: Int, centerX: Int, centerY: Int) = {
      val c = pow(x - centerX, 2) + pow(y - centerY, 2)
      c >= 230 && c <= 260
  }

  private def isInTheSmallCircle(x: Int, y: Int, centerX: Int, centerY: Int) = {
    pow(x - centerX, 2) + pow(y - centerY, 2) >= 20 &&
    y > 0                   &&
    x > 0                   &&
    y < curveImageWidth - 1 &&
    x < curveImageWidth - 1
  }

  private val pixels16 = getSquarePixels(16)


  /**
   * Method which returns an image of the specified curve
   * @param top is the curve going to the top. If it is, then true, otherwise false.
   * @param right is the curve going to the right. If it is, then true, otherwise false.
   */
  def getCurveImage(top: Boolean, right: Boolean) = {
    val img = BufferedImage(curveImageWidth, curveImageWidth, BufferedImage.TYPE_INT_ARGB)

    val outerX          = if right then 15 else 0
    val outerY          = if top   then 0  else 15
    val outerLinePixels = curve6Radius
      .map((x, y) => ((outerX - x).abs, (outerY - y).abs))

    val circleX = if right then 15 else 1
    val circleY = if top   then 1  else 15
    val innerLinePixels = pixels16
      .filter(isInTheBigCircle(_, _, circleX, circleY))

    val pavementX = if right then 16 else 0
    val pavementY = if top   then 0  else 16
    val pavementPixels  = pixels16
      .filter(isInTheSmallCircle(_, _, pavementX, pavementY))

    setPixelsColor(img, pavementPixels, pavementRGB)
    setPixelsColor(img, innerLinePixels, markingRGB)
    setPixelsColor(img, outerLinePixels, markingRGB)
    img
  }


  //The following curves are meant for a single taxiways. For example a taxiway curves from north to west
  //It can be rotated to get other directions
  private val curvedSegmentWidth = 2 * curveImageWidth
  private val d      = 8//the distance to setting the pavement pixels square from the top and and the left.
  private val pixels = getSquarePixels(2 * curveImageWidth - d)

  val northToWest = {
    val img = BufferedImage(curvedSegmentWidth, curvedSegmentWidth, BufferedImage.TYPE_INT_ARGB)
    val g   = img.createGraphics()

    val pavementPixels = pixels
      .map((x, y) => (x + d, y + d))
      .filter((x, y) => pow(x, 2) + pow(y, 2) <= 900)

    val markingPixels = pixels
      .map((x, y) => (x + d, y + d))
      .filter((x, y) =>
        pow(x, 2) + pow(y, 2) <= 700 && x < 25 &&
        pow(x, 2) + pow(y, 2) >= 660 && y < 25
      )
      ++ Vector((24, 9), (24, 8), (9, 24), (8, 24))

    setPixelsColor(img, pavementPixels, pavementRGB)
    setPixelsColor(img,  markingPixels,  markingRGB)
    g.drawImage(getCurveImage(true, false), 0, 0, null)
    g.setColor(yellowGroundMarking)
    img
  }

end TaxiwayCurves
