package GameLogic

import scala.collection.mutable.Buffer

class Air():

  private val airplanes = Buffer[Airplane]()

  def addPlane(plane: Airplane) = airplanes += plane

  def removePlane(plane: Airplane) = airplanes -= plane
  
  def allPlanesUseFuel() = airplanes.foreach(_.burnFuel())
  
  def getPlanes = airplanes.toVector

  def seeIfNoFuelLeft() =
    val crashedPlanes = Buffer[Airplane]()
    for plane <- airplanes do
      if plane.seeIfNoFuel then
        crashedPlanes += plane
    crashedPlanes.toVector
    
  def resetAir() =
    airplanes.clear()
end Air
