package FileHandling

import FileHandling.GameJSONParser.*

import scala.math.min

/**
 * Does some basic checks on the different components of the airport.
 * Note, that these methods do not guarantee that the component
 * will be in a playable state.
 * Instead it filters out the worst case scenarios.
 * If the object doesn't pass the checks, the methods return false.
 * Otherwise the methods return true.
 * 
 * These methods don't check if the object is on top of an another object,
 * since it could sometimes be the wanted outcome.
 */
class GameDataValidator(mapWidth: Int, mapHeight: Int):
  def validateTaxiways(taxiways: Vector[Taxiway]): Boolean = {
    taxiways.forall( t =>
      isInsideMap(t.startX, t.startY) &&
      isInsideMap(  t.endX,   t.endY)
    )
  }

  def validateWeather(weather: Vector[Weather], runways: Vector[Runway]) = {
    weather.map(_.startTime).distinct.length == weather.length
    &&
    weather.forall(w =>
      w.description != ""     &&
      w.startTime < w.endTime &&
      w.affectedRunways.forall(r => runways.exists(_.name == r.runwayName))
    )
  }

  def validateRunways(runways: Vector[Runway]): Boolean = {
    runways.map(_.name).distinct.length == runways.length
    &&
    runways.forall(r =>
      isInsideMap(r.startX, r.startY) &&
      isInsideMap(  r.endX,   r.endY) &&
      r.width == 30                   &&
      r.name.startsWith("R")          &&
      r.name.tail.forall(_.isDigit)   &&
      r.name.length == 3
    )
  }

  def validateGates(gates: Vector[Gate]): Boolean = {
    gates.map(_.name).distinct.length == gates.length
    &&
    gates.forall(g =>
      g.name.startsWith("G")                     &&
      (g.name.length == 2 || g.name.length == 3) &&
      g.name.tail.forall(_.isDigit)              &&
      isInsideMap(g.locationX, g.locationY)      &&
      (g.direction == "North" ||
       g.direction == "South" ||
       g.direction == "East"  ||
       g.direction == "West"
      )
    )
  }
  
  def validateAirplanes(airplanes: Vector[Airplane], longestRunwayLength: Int): Boolean = {
    airplanes.map(_.flightNumber).distinct.length == airplanes.length
    &&
    airplanes.forall(a =>
      a.passangers >= 0                                  &&
      a.passangers <= 10000                              &&
      a.minRunwayLengthForLanding <= longestRunwayLength &&
      a.minRunwayLengthForTakeoff <= longestRunwayLength &&
      a.flightNumber.toString.length == 3                &&
      a.maxFuel >= a.currentFuel                         &&
      a.currentFuel >= 5 //so plane doesn't crash straight away
    )
  }

  private def isInsideMap(x: Int, y: Int): Boolean = {
    x >= 0 && x <= mapWidth && y >= 0 && y <= mapHeight
  }
end GameDataValidator
