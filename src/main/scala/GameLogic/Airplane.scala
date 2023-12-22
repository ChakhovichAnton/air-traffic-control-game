package GameLogic

import FileHandling.FileHandlingHelperMethods.readImage
import java.awt.image.BufferedImage

class Airplane(val flightNumber: Int,
               val passangers: Int,
               val maxFuel: Int,
               var startFuel: Int,
               val minimumRunwayLengthForTakeoff: Int,
               val minimumRunwayLengthForLanding: Int,
               val imageSource: String
              ):

  private var coordinates: Option[(Int, Int)] = None
  private var currentFuel      = startFuel
  private var totalWaitingTime = 1//use 1 here to avoind null pointer exception just in case
  private var fuelUsed         = 1//use 1 here to avoind null pointer exception just in case
  private var facing:Option[Direction] = None

  private var width  = 20//standard with and height
  private var height = 20

  def getWidth = width

  def getHeight = height

  /**
   * If an another plane shares at least 1 common pixel, a crash happens.
   * This variable has the pixels of the image. Top left corner is the 0,0
   */
  val crashPixels = {
    val img = {
      readImage(s"Files/$imageSource") match
       case Some(img) => img
       case None => BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    }

    width = img.getWidth
    height = img.getHeight
    require(img.getWidth == 20)
    require(img.getHeight == 20)

    val pxs = {
      for
       x <- (0 until width).toVector
       y <- 0 until height
      yield
       (x, y)
    }

    pxs.filterNot((x, y) => img.getRGB(x, y) == 0)
  }
  
  def resetAirplane() =
    coordinates = None
    currentFuel = this.startFuel
    totalWaitingTime = 1
    fuelUsed = 0
    facing = None
  
  def removeCoordinates() =
    facing = None
    coordinates = None

  def setCoordinates(x: Int, y: Int) = coordinates = Some((x, y))
  
  def setCoordinates(c: (Int, Int)) = coordinates = Some(c)

  def setDirection(dir: Direction) = facing = Some(dir)
  
  def getDirection = facing
  
  def moveForwardX() =
    coordinates match
      case Some((x,y)) => coordinates = Option((x + 1, y))
      case None => ()
      
  def moveForwardY() =
    coordinates match
      case Some((x,y)) => coordinates = Option((x, y + 1))
      case None => ()

  def getCoordinates = coordinates

  def getFuel:Int = currentFuel

  def seeIfNoFuel = currentFuel == 0

  def burnFuel() =
    currentFuel -= 1
    fuelUsed += 1

  def getUsedFuel:Int = fuelUsed
  
  def getWaitingTime = totalWaitingTime

  def addWaitingTime() = totalWaitingTime += 1

end Airplane
