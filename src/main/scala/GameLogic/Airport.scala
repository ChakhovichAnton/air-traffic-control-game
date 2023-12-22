package GameLogic

import scala.collection.mutable.Buffer

class Airport(val air     : Air,
              val runways : Vector[Runway],
              val taxiways: Taxiway,
              val gates   : Vector[Gate],
              airplanes   : Vector[(Int, Airplane)],
              val weather : Vector[Weather]
             ):
  private var mapWidth  = 600 //standard width
  private var mapHeight = 600 //standard height
  
  val statistics = Statistics()

  def setMapDimensions(width: Int, height: Int) =
    mapWidth  = width
    mapHeight = height

  def getPlanes = airplanes.map(_._2)
    
  def getPlaneByPlaneNumber(number: Int) =
    getPlanes.find(_.flightNumber == number)
  
  def getRunwaysByRunwayName(name: String) =
    runways.find(_.name.toLowerCase == name.toLowerCase)

  def getPlanesReadyAtGates(currentTime: Int): Vector[(Gate, Airplane)] =
    val readyGates = gates.filter(_.getIfReady(currentTime).isDefined)
    readyGates.map(gate => (gate, gate.getIfReady(currentTime).get))

  def seeIfGameEnds =
    statistics.departuresPlaneCount >= airplanes.length ||
    !statistics.noCrash

  /**
   * Planes queuing for takeoff or waiting for a gate
   */
  def getWaitingRunwayPlanes =
    val queuePlanes = runways.flatMap(_.takeoffQueue.getPlanesInTheQueue)

    val waitingForGate = {
      val planes = runways.map(_.isWaitingForGate).filter(_.isDefined)
      if planes.isEmpty then
        Vector[Airplane]()
      else
        planes.map(_.get)
    }

    queuePlanes ++ waitingForGate
  end getWaitingRunwayPlanes

  /**
   * @return The runways and the point (x,y) in which the runways intersect
   */
  def getIntersectingRunways: Vector[(Runway, Runway, Int, Int)] =
    val xDirRunways = runways.filter(_.runwayXDirection)
      .map(r => (r.startX to r.endX, r.startY, r))
    val yDirRunways = runways.filterNot(_.runwayXDirection)

    val intersectingRunways = Buffer[(Runway, Runway, Int, Int)]()
    for (xRange, y, xR) <- xDirRunways do
      for yR <- yDirRunways do
        xRange.find(_ == yR.startX) match
          case Some(x) if !intersectingRunways
              .map(n => ((n._1, n._2)))
              .contains((xR, yR))
            =>
              intersectingRunways += ((xR, yR, x, y))
          case _ => ()
    intersectingRunways.toVector
  end getIntersectingRunways

  def deployAirplanes(time: Int) =
    for
      plane <- airplanes.filter(_._1 == time).map(_._2)
    do
      air.addPlane(plane)
  end deployAirplanes


  def removePlanesThatAreOutside() =
    for plane <- getPlanes do
        plane.getCoordinates match
          case Some((x,y)) if x > mapWidth + 10 || x < -10 || y < - 10 || y > mapHeight + 10 =>
            plane.removeCoordinates()
            statistics.addDeparture(plane)
            runways.find(_.getPlanesTakeoff.contains(plane)) match
              case Some(r) =>
                r.removePlaneAfterTakeoff(plane)
              case None => ()
          case _ => ()
  end removePlanesThatAreOutside


  /**
   * Currently this method takes quite some time to execute.
   * It is due to the marked part. If there's any lag, 
   * this method is to blame
   */
  def seeIfCrash() =
    val planesUsingTheRunway:Vector[(Airplane, Runway)] = {
      runways
        .map(r => (r.getPlanesOnTheRunway, r))
        .flatMap((v, r) => (v.map(p => (p, r))))
        .filter(_._1.getCoordinates.isDefined)//the coordinates are used in the next for loop
    }                                         //and this checks that they exist

    val planeCombinations:Vector[(Airplane, Airplane)] = {
      planesUsingTheRunway.map(_._1)
        .combinations(2).toVector.map(n => (n.head, n(1)))
    }

    val planeCombinationsWithCoordinates = {
      planeCombinations
        .filter(_._1.getCoordinates.isDefined)
        .filter(_._1.getCoordinates.isDefined)
    }
    
    def findRunwayName(airplane: Airplane) = {
      planesUsingTheRunway.find(_._1 == airplane) match
        case Some((p, r)) => r.name
        case None    => "*Name Not Found*"
    }
    
    for
      (p1, p2) <- planeCombinationsWithCoordinates
    do
      val (p1X, p1Y) = p1.getCoordinates.get//checked above that coordinates exist
      val (p2X, p2Y) = p2.getCoordinates.get

      val p1Pixels = {
        p1.crashPixels
          .map((x, y) => (x + p1X, y + p1Y))
          .toList//lists are faster go through
      }
      val p2Pixels = {
        p2.crashPixels
          .map((x, y) => (x + p2X, y + p2Y))
          .toList//lists are faster go through
      }

      if
        p1 != p2 &&
        p1Pixels.exists((x, y) => p2Pixels.contains((x, y)))//this takes for a "long" time
      then
        statistics
          .addCrash(p1, s"${p1.flightNumber} collided with a airplane on runway ${findRunwayName(p1)}.")
        statistics
          .addCrash(p2, s"${p2.flightNumber} collided with a airplane on runway ${findRunwayName(p2)}.")
    end for
  end seeIfCrash

end Airport
