package GameLogic

import scala.collection.mutable.Buffer

/**
 * Class representing taxiways
 * @param timeSpentTaxiing the time a plane spends taxiing
 */
class Taxiway(val timeSpentTaxiing: Int):
  private val airplanes = Buffer[(Int, Airplane, Destination)]()
  //The buffer's int is the time when the plane finishes taxiing

  def getPlanes =
    airplanes.map(n => (n._1, n._2, taxiingMessage(n._2, n._3))).toVector
  
  def getPlanesTaxiingToRunway =
    airplanes.filter(!_._3.seeIfGate).map(n => n._2).toVector

  def getPlanesTaxiingTo(runway: Runway) =
    var planes = Buffer[Airplane]()
    for
      (time, airplane, destination) <- airplanes
    do
      destination match
        case r: destRunway if r.runway == runway =>
          planes += airplane
        case _ => ()
    end for

    planes
  end getPlanesTaxiingTo

  def addPlane(time: Int, plane: Airplane, gate: Gate) =
    airplanes += ((time + timeSpentTaxiing, plane, destGate(gate)))

  def addPlane(time: Int, plane: Airplane, runway: Runway) =
    airplanes += ((time + timeSpentTaxiing, plane, destRunway(runway)))

  private def getReadyPlanes(currentTime: Int,
                             planes: Buffer[(Int, Airplane, Destination)]
                            ) =
    
    val finished =
      planes.filter((time, plane, destination) => time == currentTime).toVector
    
    finished.foreach(airplanes -= _)
    finished
  end getReadyPlanes

  /**
   * Returns the taxiing message which appears on the user interface
   */
  private def taxiingMessage(plane: Airplane, destination: Destination) =
    val destinationName = destination match
      case gate: destGate => gate.gate.name
      case runway: destRunway => runway.runway.name
    s"Taxiing to $destinationName"
  end taxiingMessage

  def getPlanesAtGate(currentTime: Int):Vector[(Gate, Airplane)] =
    val readyPlanes = {
      getReadyPlanes(
        currentTime,
        airplanes.filter((time, plane, destination) => destination.seeIfGate)
      )
    }
    val planeAtGateBuffer = Buffer[(Gate, Airplane)]()

    for
      (time, plane, destination) <- readyPlanes
    do
      destination match
        case gate: destGate     =>
          planeAtGateBuffer += ((gate.gate, plane))
        case runway: destRunway => ()
    end for

    planeAtGateBuffer.toVector
  end getPlanesAtGate

  def getPlanesAtRunway(currentTime: Int):Vector[(Runway, Airplane)] =
    val readyPlanes = {
      getReadyPlanes(
        currentTime,
        airplanes.filter((time, plane, destination) => !destination.seeIfGate)
      )
    }
    val planesAtRunwayBuffer = Buffer[(Runway, Airplane)]()

    for
      (time, plane, destination) <- readyPlanes
    do
      destination match
        case runway: destRunway =>
          planesAtRunwayBuffer += ((runway.runway, plane))
        case gate: destGate     => ()
    end for

    planesAtRunwayBuffer.toVector
  end getPlanesAtRunway
    
  def resetTaxiway() =
    airplanes.clear()


  //Destinations as case classes:
  sealed abstract class Destination:
    def seeIfGate = this match
      case gate  : destGate   => true
      case runway: destRunway => false

  private case class destRunway(runway: Runway) extends Destination
  private case class destGate(gate: Gate)       extends Destination

end Taxiway
