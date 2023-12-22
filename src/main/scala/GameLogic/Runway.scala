package GameLogic

import scala.math.{max, min}
import scala.collection.mutable.Buffer

/**
 *Planes use runways to takeoff and land. Runways must have a
 * width of 30 and the length must be over 30.
 * @param startX x coordinate of top right corner of runway
 * @param startY y coordinate of top right corner of runway
 * @param endX x coordinate of bottom left corner of runway
 * @param endY y coordinate of bottom left corner of runway
 * @param name name of the runway must start with the letter R.
 *             It's length must be 2 letters/numbers excluding
 *             the R prefix. Total length of the name is three.
 */
class Runway(val startX:Int,
             val startY: Int,
             val endX  : Int,
             val endY  : Int,
             val name  : String
            ):

  private val usingTheRunway = Buffer[(Airplane, Action)]()//Action is takeoff or landing
  private var waitingForGate:Option[Airplane] = None

  //if there's an active weather condition, the runway is closed and all planes will crash
  private var weather: Option[Weather]   = None
  private var weatherStartUsingTheRunway = Vector[Airplane]()
  //the vector has planes that were using the runway at the time the weather condition started
  //this is so that these planes don't crash

  //true if runway's length is paraller to the x direction. Otherwise false
  val runwayXDirection = endX - startX > endY - startY

  val length = max(endX - startX, endY - startY)
  val width  = min(endX - startX, endY - startY)

  private val planeDistanceFromEdge = 5//from the runway's edge
  val outsideMapCoordinates = {//point from where the planes starts to land
    if runwayXDirection then
      (0, startY + planeDistanceFromEdge)
    else
      (startX + planeDistanceFromEdge, 0)
  }

  private val stopDistanceFromEnd = 20
  private val stopCoordinates     = {
    if runwayXDirection then
      endX - stopDistanceFromEnd
    else
      endY - stopDistanceFromEnd
  }
  private val takeoffCoordinates = {
    if runwayXDirection then
      (startX, startY + planeDistanceFromEdge)
    else
      (startX + planeDistanceFromEdge, startY)
  }

  //The following are the directions in which the planes front points at
  val takeoffPlaneDirection:Direction = if runwayXDirection then East() else South()
  val landingPlaneDirection:Direction = if runwayXDirection then East() else South()


  private val maxQueueSize = 3
  val takeoffQueue = RunwayQueue(maxQueueSize, runwayXDirection, startX, startY)

  private var takeoffCooldown = 0

  def reduceTakeoffCooldownEverySecond() =
    if takeoffCooldown > 0 then takeoffCooldown -= 1

  def canPlaneTakeoff = takeoffCooldown == 0

  def startWeather(weather: Weather) =
    weatherStartUsingTheRunway = {
      waitingForGate match
        case Some(a) => Vector(a) ++ usingTheRunway.map(_._1).toVector
        case None   => usingTheRunway.map(_._1).toVector
    }
    this.weather = Some(weather)
  end startWeather

  def endWeather() =
    weather = None

  def getWeatherDescription = weather.map(_.description)
  
  def getWeather = weather

  def resetRunway() =
    usingTheRunway.clear()
    takeoffQueue.reset()
    waitingForGate = None
    weather        = None
  end resetRunway

  def isWaitingForGate = waitingForGate
  
  def removePlaneAfterTakeoff(plane: Airplane) =
    usingTheRunway.find(_._1 == plane) match
      case Some(p) => usingTheRunway -= p
      case None    => ()

  def removeWaitingForGate() =
    waitingForGate = None

  def getPlanesTakeoff =
    usingTheRunway.filter(_._2.seeIfTakeoff).map(_._1).toVector

  def getPlanesLanding =
    usingTheRunway.filter(!_._2.seeIfTakeoff).map(_._1).toVector

  /**
   * Method returns all planes that are on the runway. If a plane is
   * located after or before the runway (in the air), it does not
   * get returned
   */
  def getPlanesOnTheRunway: Vector[Airplane] =
    val airplanes = Buffer[Airplane]()
    for
      plane <- usingTheRunway.map(_._1)
    do
      plane.getCoordinates match
        case Some((x, y))
          if
            (runwayXDirection             &&
             x + plane.getWidth >= startX &&
             x <= endX)
            ||
            (!runwayXDirection             &&
             y + plane.getHeight >= startY &&
             y <= endY)
          =>
            airplanes += plane
        case _ => ()
    end for

    val waitingForGateOnRunway = {
      waitingForGate match
        case Some(p: Airplane) => Vector(p)
        case _                 => Vector()
    }
    airplanes.toVector ++ waitingForGateOnRunway
  end getPlanesOnTheRunway

  def land(plane: Airplane) =
    usingTheRunway += ((plane, Landing()))
    plane.setCoordinates(outsideMapCoordinates)
    plane.setDirection(landingPlaneDirection)
  end land

  def takeoff(): Boolean =
    takeoffQueue.takeoff() match
      case Some(plane) =>
        usingTheRunway += ((plane, Takeoff()))
        plane.setCoordinates(takeoffCoordinates)
        plane.setDirection(takeoffPlaneDirection)
        takeoffCooldown = 5//after 5 seconds, a plane can takeoff again
        true
      case None =>
        false
  end takeoff

  def moveThePlanes(): Vector[(Airplane, String)] =
    /**
     * x and y are the planes coordinates
     */
    def weatherCrash(x: Int, y: Int, plane: Airplane): Boolean = {
      weather.isDefined &&
      !weatherStartUsingTheRunway.contains(plane) &&
      (
        (runwayXDirection  && x >= endX) ||
        (!runwayXDirection && y >= endY)
      )
    }

    if runwayXDirection then
      usingTheRunway.foreach(_._1.moveForwardX())
    else
      usingTheRunway.foreach(_._1.moveForwardY())

    val crashedPlanes = Buffer[(Airplane, String)]()
    for
      plane <- getPlanesTakeoff
    do
      plane.getCoordinates match
        case Some((x, y))
          if plane.minimumRunwayLengthForTakeoff > length &&
            (
             (runwayXDirection  && x >= endX) ||
             (!runwayXDirection && y >= endY)
            )
        =>
          crashedPlanes += ((plane, "Takeoff, runway too short"))

        case Some((x, y)) if weatherCrash(x, y, plane) =>
          crashedPlanes += ((plane, s"Plane crashed due to ${weather.get.description}"))

        case _ => ()
    end for

    crashedPlanes.toVector
  end moveThePlanes

  def reactIfLandingPlanesHaveReachedTheEnd(): Vector[(Airplane, String)] =
    def reachedEndCoordinates(x: Int, y: Int) = {
      if runwayXDirection then
        x >= stopCoordinates
      else
        y >= stopCoordinates
    }

    val crashedPlanes = Buffer[(Airplane, String)]()
    for
      (plane, action) <- usingTheRunway.filter(!_._2.seeIfTakeoff)
    do
      plane.getCoordinates match
        case Some((x,y)) if reachedEndCoordinates(x, y) =>
          if
            plane.minimumRunwayLengthForLanding > length
          then
            crashedPlanes += ((plane, "Landing, runway too short"))
          else if
            weather.isDefined && !weatherStartUsingTheRunway.contains(plane)
          then
            crashedPlanes += ((plane, s"Plane crashed due to ${weather.get.description}"))
          else
            usingTheRunway -= ((plane, action))
            waitingForGate = Some(plane)
        case _ => ()
    end for

    crashedPlanes.toVector
  end reactIfLandingPlanesHaveReachedTheEnd


  //How the plane uses the runway:
  sealed abstract class Action:
    def seeIfTakeoff = this match
      case _: Landing => false
      case _: Takeoff => true

  private case class Takeoff() extends Action
  private case class Landing() extends Action

end Runway
