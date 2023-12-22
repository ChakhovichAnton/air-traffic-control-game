package GameLogic

class Game(val airport: Airport, val scenarioName: String):
  private var timeInSeconds = 0
  val inputHandler = InputHandler(airport)

  private val stats    = airport.statistics
  private val air      = airport.air
  private val taxiways = airport.taxiways
  private val runways  = airport.runways
  private val gates    = airport.gates
  private val weather  = airport.weather

  stats.setScenarioName(scenarioName)


  def addSecond() =
    timeInSeconds += 1
    stats.setCurrentTime(timeInSeconds)

    air.allPlanesUseFuel()
    stats.addCrashes(airport.air.seeIfNoFuelLeft()
      .map(((_, "Ran out of fuel"))))
    airport.deployAirplanes(timeInSeconds)

    airport.getWaitingRunwayPlanes.foreach(_.addWaitingTime())
    airport.getPlanesReadyAtGates(timeInSeconds)
      .foreach(_._2.addWaitingTime())

    taxiways.getPlanesAtGate(timeInSeconds)
      .foreach(_.addPlane(_, timeInSeconds))

    taxiways.getPlanesAtRunway(timeInSeconds)
      .foreach(_.takeoffQueue.queueToTheRunway(_))
    
    weather.foreach(_.reactIfStartTime(timeInSeconds))
    weather.foreach(_.reactIfEndTime(timeInSeconds))
    runways.foreach(_.reduceTakeoffCooldownEverySecond())
  end addSecond


  def onTick() =
    for runway <- airport.runways do
      stats.addCrashes(runway.reactIfLandingPlanesHaveReachedTheEnd())
      stats.addCrashes(runway.moveThePlanes())
    end for

    airport.seeIfCrash()
    airport.removePlanesThatAreOutside()
  end onTick


  def resetGame() =
    stats.resetStatistics()
    air.resetAir()
    gates.foreach(_.resetGate())
    runways.foreach(_.resetRunway())
    taxiways.resetTaxiway()
    airport.getPlanes.foreach(_.resetAirplane())
    timeInSeconds = 0
  end resetGame


  def getTime = timeInSeconds

end Game
