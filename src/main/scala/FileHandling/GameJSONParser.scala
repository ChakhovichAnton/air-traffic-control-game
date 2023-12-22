package FileHandling

import FileHandling.GameDataValidator

import scala.util.{Failure, Success, Try}
import argonaut.*
import Argonaut.*
import scala.math.{max, min}
import swing.Color
import scala.collection.mutable.Buffer
import java.io.*


object GameJSONParser:
  case class Airport(runways  : List[Runway],
                     gates    : List[Gate],
                     airplanes: List[Airplane],
                     taxiways : List[Taxiway],
                     surfaces : List[Surface],
                     images   : List[Image],
                     weather  : List[Weather]
                    )

  case class Gate(name     : String,
                  locationX: Int,
                  locationY: Int,
                  direction: String
                 )

  case class Runway(startX: Int,
                    startY: Int,
                    endX  : Int,
                    endY  : Int,
                    name  : String
                   ) {
    val width  = min(endX - startX, endY - startY)
    val length = max(endX - startX, endY - startY)
  }

  case class Taxiway(startX: Int,
                     startY: Int,
                     endX  : Int,
                     endY  : Int
                    )

  case class Airplane(time                     : Int,
                      flightNumber             : Int,
                      maxFuel                  : Int,
                      currentFuel              : Int,
                      passangers               : Int,
                      minRunwayLengthForTakeoff: Int,
                      minRunwayLengthForLanding: Int,
                      imageFileName            : String
                     )

  case class Surface(startX: Int,
                     startY: Int,
                     endX  : Int,
                     endY  : Int,
                     colorR: Int,
                     colorG: Int,
                     colorB: Int
                    )

  case class Image(x: Int, y: Int, source: String)

  case class Weather(description    : String,
                     startTime      : Int,
                     endTime        : Int,
                     affectedRunways: List[AffectedRunway]
                    )

  case class AffectedRunway(runwayName: String)

  object Airport:
    implicit def AirportDecodeJson: DecodeJson[Airport] =
      jdecode7L(Airport.apply)(
        "runways",
        "gates",
        "airplanes",
        "taxiways",
        "surfaces",
        "images",
        "weather"
      )

  object Gate:
    implicit def GateDecodeJson: DecodeJson[Gate] =
      jdecode4L(Gate.apply)(
        "name",
        "locationX",
        "locationY",
        "direction"
      )

  object Runway:
    implicit def RunwayDecodeJson: DecodeJson[Runway] =
      jdecode5L(Runway.apply)(
        "startX",
        "startY",
        "endX",
        "endY",
        "name"
      )

  object Taxiway:
    implicit def TaxiwayDecodeJson: DecodeJson[Taxiway] =
      jdecode4L(Taxiway.apply)(
        "startX",
        "startY",
        "endX",
        "endY"
       )

  object Airplane:
    implicit def AirplaneDecodeJson: DecodeJson[Airplane] =
      jdecode8L(Airplane.apply)(
        "time",
        "flightNumber",
        "maxFuel",
        "currentFuel",
        "passangers",
        "minRunwayLengthForTakeoff",
        "minRunwayLengthForLanding",
        "imageFileName"
      )

  object Surface:
    implicit def SurfaceDecodeJson: DecodeJson[Surface] =
      jdecode7L(Surface.apply)(
        "startX",
        "startY",
        "endX",
        "endY",
        "colorR",
        "colorG",
        "colorB"
       )

  object Image:
    implicit def ImageDecodeJson: DecodeJson[Image] =
      jdecode3L(Image.apply)("x", "y", "source")

  object Weather:
    implicit def WeatherDecodeJson: DecodeJson[Weather] =
      jdecode4L(Weather.apply)(
        "description",
        "startTime",
        "endTime",
        "affectedRunways"
      )

  object AffectedRunway:
    implicit def AffectedRunwayDecodeJson: DecodeJson[AffectedRunway] =
      jdecode1L(AffectedRunway.apply)("runwayName")


  private def readJsonFile(source: String):Option[Airport] =
    val path = "/Resources/GameScenarios/" + source
    val stream: InputStream = getClass.getResourceAsStream(path)

    try {
      val lines: Iterator[String] = scala.io.Source.fromInputStream(stream).getLines
      val ap = lines.mkString.decodeOption[Airport]
      stream.close()
      ap
    } catch {
      case _:Throwable => None
    }
  end readJsonFile


  private def makeGame(airport: Airport, mapHeight: Int, mapWidth: Int, path: String) =

    def getScenarioName(path: String) = {
      path
        .takeRight(path.length - path.lastIndexWhere(_.toString == """\""") - 1)
        .takeWhile(_ != '.')
        .capitalize
    }

    val timeAtGate  = 15
    val timeTaxiing = 15

    val v = GameDataValidator(mapWidth, mapHeight)
    val longestRunwayLength = airport.runways.toVector.map(_.length).reduceLeft(max)

    val allRunwaysValid  = v.validateRunways(airport.runways.toVector)
    val allGatesValid    = v.validateGates(airport.gates.toVector)
    val allTaxiwaysValid = v.validateTaxiways(airport.taxiways.toVector)
    val allWeatherValid  = v.validateWeather(airport.weather.toVector, airport.runways.toVector)
    val allPlanesValid   = v.validateAirplanes(
      airport.airplanes.toVector,
      longestRunwayLength
    )

    if
      allPlanesValid  &&
      allRunwaysValid &&
      allGatesValid   &&
      allWeatherValid &&
      allTaxiwaysValid
    then
      val gates = airport.gates.toVector.map(gate => GameLogic.Gate(
        gate.name,
        timeAtGate,
        gate.locationX,
        gate.locationY,
        gate.direction match
          case "North" => GameLogic.North()
          case "South" => GameLogic.South()
          case "East"  => GameLogic.East()
          case "West"  => GameLogic.West()
      ))

      val runways = {
        airport.runways.toVector.map(runway => GameLogic.Runway(
          runway.startX,
          runway.startY,
          runway.endX,
          runway.endY,
          runway.name
        ))
      }

      val taxiways =
        airport.taxiways.toVector.map(t => ((t.startX, t.startY, t.endX, t.endY)))

      val airplanes: Vector[(Int, GameLogic.Airplane)] = {
        airport.airplanes.toVector.map(plane =>
          (plane.time, GameLogic.Airplane(
            plane.flightNumber,
            plane.passangers,
            plane.maxFuel,
            plane.currentFuel,
            plane.minRunwayLengthForTakeoff,
            plane.minRunwayLengthForLanding,
            plane.imageFileName
            )
          )
        )
      }

      val surfaces = {
        airport.surfaces.toVector.map(s => (
          s.startX,
          s.startY,
          s.endX,
          s.endY,
          new scala.swing.Color(s.colorR, s.colorG, s.colorB))
        )
      }

      val images = {
        airport.images.toVector.map(i =>
          (i.x,
           i.y,
           i.source
          )
        )
      }

      val weather = {
        try//executing this in a try just in case for some reason the validator has not worked
          airport.weather.toVector.map(i =>
            GameLogic.Weather(
              i.description,
              i.startTime,
              i.endTime,
              i.affectedRunways//we can use get since the name has been checked above in the validations
                .map(r => runways.find(_.name == r.runwayName).get)
                .toVector
            )
          )
        catch
          case e: NoSuchElementException =>//happens when .get is called on an empty collection
            Vector[GameLogic.Weather]()
      }

      val gameLogicAirport = GameLogic.Airport(
        GameLogic.Air(),
        runways,
        GameLogic.Taxiway(timeTaxiing),
        gates, 
        airplanes,
        weather
      )
      val gameLogcGame = GameLogic.Game(gameLogicAirport, getScenarioName(path))
      Some((gameLogcGame, taxiways, surfaces, images))
    else
      None
  end makeGame


  def loadGame(path: String, mapHeight: Int, mapWidth: Int) =
    readJsonFile(path) match
      case Some(ap) =>
        makeGame(ap, mapHeight, mapWidth, path)
      case None =>
        None
end GameJSONParser

