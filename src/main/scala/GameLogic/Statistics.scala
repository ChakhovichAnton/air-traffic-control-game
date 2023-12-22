package GameLogic

import scala.collection.mutable.Buffer

/**
 * Calculates points and statistics.
 * If a crash happens, all points are zero
 */
class Statistics():
  private val arrivals      = Buffer[Airplane]()
  private val departures    = Buffer[Airplane]()
  private val crashes       = Buffer[(Airplane, String)]()//the string is extra information on the crash
  private var currentTime   = 0
  private var scenarioName  = ""

  def setCurrentTime(time: Int) = currentTime = time

  def setScenarioName(name: String) = scenarioName = name

  def addDeparture(airplane: Airplane) = departures += airplane
  
  def addArrival(airplane: Airplane) = arrivals += airplane
  
  def getDepartedPlanes = departures.toVector
  
  def arrivalsPlaneCount:Int = arrivals.size
  
  def departuresPlaneCount:Int = departures.size
  
  def casualtyCount:Int = crashes.map(_._1.passangers).sum
  
  def crashedPlaneCount = crashes.size

  def addCrash(plane: Airplane, reason: String) =
    if !crashes.map(_._1).contains(plane) then crashes += ((plane, reason))
    
  def addCrashes(planes: Vector[(Airplane, String)]) =
    planes.foreach(p => addCrash(p._1, p._2))

  def noCrash = crashes.isEmpty
  
  def resetStatistics() =
    arrivals.clear()
    departures.clear()
    crashes.clear()
  
  def arrivalsPoints:Int =
    if noCrash then
      try
        arrivals.map(p => p.passangers/p.getUsedFuel).sum
      catch
        case e: NullPointerException => 0
    else
      0

  /**
   * Calculates the points from departures. A planes waiting time is divided by five
   * since otherwise that points would be too small compared to the points from arrivals
   */
  def departuresPoints:Int =
    if noCrash then
      try
        departures.map(p => p.passangers/(p.getWaitingTime/ 5)).sum
      catch
        case e: NullPointerException => 0
    else
      0

  def totalPoints:Int =
    if noCrash then
      (arrivalsPoints + departuresPoints)/2
    else
      0

  def getScoreReport:Vector[String] =
    val accidentText =
      if noCrash then
        Vector("No planes crashed. Congratulations!")
      else
        Vector(
          s"There were a total of $crashedPlaneCount crashes and " +
            s"$casualtyCount casualties ",
            "unfortunately this many crashes is unacceptable and" +
            " your coworked had to take over your shift.",
          "The following is a list of all crashed planes and reasons " +
            "for their crashes. Study and learn from them!",
          ""
        ) ++ crashes.map(n => "Plane number " + n._1.flightNumber + ": " + n._2)
    
    Vector(
       s"Scenario: $scenarioName",
      s"The game took $currentTime seconds.",
      s"${arrivals.size} planes and ${arrivals.map(_.passangers).sum} managed " +
        s"to land safely. You were awarded $arrivalsPoints points for landings!",
      s"${departures.size} planes and ${departures.map(_.passangers).sum} managed " +
        s"to takeoff safely. You were awarded $departuresPoints points for takeoffs!",
      s"Your final score is $totalPoints points!",
      ""
    ) ++ accidentText
  end getScoreReport

end Statistics
