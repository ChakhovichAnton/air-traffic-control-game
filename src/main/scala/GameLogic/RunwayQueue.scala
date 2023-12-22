package GameLogic

import scala.collection.mutable.Queue

/**
 * Class representing planes queueing to runway for takeoff
 * @param maxSize the amount of planes a queue can have at a time
 * @param runwayXDirection is the runway paraller to x-axis
 * @param startX runway's top left x coordinate
 * @param startY runway's top left y coordinate
 */
class RunwayQueue(val maxSize     : Int,
                  runwayXDirection: Boolean,
                  startX          : Int,
                  startY          : Int
                 ):
  
  private val takeoffQueue = Queue[Airplane]()

  val queueAreaFromRunwayStart = 10//The distance from the end of the runway to the center of the queue area
  //The following coordinates are places in which the planes queue
  private val startCoordMap  = Map(true -> startY, false -> startX)

  //The coordinate where the planes queue
  private val queueCoord1FromRunway = startCoordMap(runwayXDirection) + 55
  private val queueCoord2FromRunway = startCoordMap(runwayXDirection) + 80
  private val queueCoord3FromRunway = startCoordMap(runwayXDirection) + 105
  
  val queuingPlaneDirection:Direction = if runwayXDirection then North() else West()

  private val queuePosMap = {
    Map(0 -> queueCoord1FromRunway,
        1 -> queueCoord2FromRunway,
        2 -> queueCoord3FromRunway
       )
  }

  val queueAreaLength = queueCoord3FromRunway - queueCoord1FromRunway + 25

  private def getQueueCenterCoord(airplane: Airplane) = {//the coordinates of the airplane are at the top left of it
    startCoordMap(!runwayXDirection) + queueAreaFromRunwayStart - airplane.getWidth / 2
  }

  private def queueCoords(airplane: Airplane, pos: Int) = {
    val coords = (getQueueCenterCoord(airplane), queuePosMap(pos))
    if runwayXDirection then coords else coords.swap
  }
  
  def getPlanesInTheQueue = takeoffQueue.toVector
  
  def moveToQueueCoord() =
    var index = 0
    for plane <- takeoffQueue do
      plane.setDirection(queuingPlaneDirection)
      plane.setCoordinates(queueCoords(plane, index))
      index += 1
  end moveToQueueCoord

  def takeoff(): Option[Airplane] =
    takeoffQueue.headOption match
      case Some(plane) =>
        takeoffQueue.dequeue()
        moveToQueueCoord()
        Some(plane)
      case None =>
        None
  end takeoff
  
  def currentQueueSize = takeoffQueue.size
  
  def queueToTheRunway(plane: Airplane) =
    takeoffQueue.enqueue(plane)
    moveToQueueCoord()

  def removeFromQueue(plane: Airplane) =
    takeoffQueue -= plane
    moveToQueueCoord()

  def reset() =
    takeoffQueue.clear()
end RunwayQueue
