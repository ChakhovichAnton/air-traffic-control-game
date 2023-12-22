package GameLogic

/* Input format:
The first three digits determine the airplanes plane number.
The fourth digit determines the planes action.
T for taxi and L for landing.

The last digits determine the exact destination.
This could be a gates number to a runways name.
Takeoff works with TR90N where T means takeoff and
the next letters/numbers define the name of the runway.

Examples: 123LR1, TR1, 123TR1, 123TG1
*/

class InputHandler(airport: Airport):
  private val runways = airport.runways
  private val gates   = airport.gates
  private val taxiway = airport.taxiways
  private val air     = airport.air

  def reactToInput(input: String, currentTime: Int) =
    var isInputValid = true
    var possibleErrorText = ""
    val trimmedInput = input.trim.toLowerCase
    val planeNumber = trimmedInput.takeWhile(_.isDigit)

    if trimmedInput.startsWith("t") then
      takeoff(trimmedInput.tail)
    else if planeNumber.length == 3 then
      airport.getPlaneByPlaneNumber(planeNumber.toInt) match//seeing if plane has been
        //added to statistics. A plane can't be directed if it has been added
        case Some(plane) =>
          matchAction(plane, trimmedInput.drop(3))

        case None =>
          possibleErrorText = s"No such plane with the number $planeNumber"
    else if planeNumber.nonEmpty then
      possibleErrorText = "A plane number must be 3 digits"
    else
      possibleErrorText = "Incorrect start of action"
    end if


    /**
     * Method figures the action of the plane and calls the correct
     * action method
     */
    def matchAction(plane: Airplane, restOfInput: String) =
      restOfInput.headOption match
        case Some('l') =>
          land(plane, restOfInput.tail)

        case Some('t') =>
          val possibleRunway =
            airport.getRunwaysByRunwayName(restOfInput.tail.toLowerCase)
          val possibleGate =
            gates.find(_.name.toLowerCase == restOfInput.tail.toLowerCase)

          if possibleRunway.isDefined then
            taxiRunway(plane, possibleRunway.get)

          else if possibleGate.isDefined then
            taxiGate(plane, possibleGate.get)
          else
            possibleErrorText = s"Location was not found"

        case Some(_) =>
          possibleErrorText ="A plane can't do that"

        case None =>
          possibleErrorText = "No action specified"
      end match
    end matchAction


    def land(plane: Airplane, location: String) =
      val planeIsInTheAir =
        air.getPlanes.exists(_.flightNumber == plane.flightNumber)

      airport.getRunwaysByRunwayName(location) match
        case Some(runway) if planeIsInTheAir =>
          air.removePlane(plane)
          runway.land(plane)
        case Some(runway) =>
          possibleErrorText = "Plane is not in the air"
        case _ =>
          possibleErrorText = "Invalid runway"
    end land


    def takeoff(runway: String) =
      airport.getRunwaysByRunwayName(runway) match
        case Some(runway) if !runway.canPlaneTakeoff =>
          possibleErrorText = "A plane can't takeoff right after another"
        case Some(runway) if runway.takeoff() => ()
        //the takeoff() method makes a plane takeoff if there is one,
        //in such case it returns true. No more actions are needed
        case Some(_) =>
          possibleErrorText = "No planes in the queue"
        case None =>
          possibleErrorText = "Invalid runway."
    end takeoff


    def taxiRunway(plane: Airplane, runway: Runway) =
      val queue = runway.takeoffQueue

      def queueToRunway: Boolean =
        val queuePlaneCount =
          queue.currentQueueSize + airport.taxiways.getPlanesTaxiingTo(runway).size

        if queuePlaneCount < queue.maxSize then
          plane.removeCoordinates()
          airport.taxiways.addPlane(currentTime, plane, runway)
          true
        else
          possibleErrorText = "Runway has no queue space"
          false
      end queueToRunway


      val possibleGate = {
        airport.getPlanesReadyAtGates(currentTime)
          .find(n => n._2.flightNumber == plane.flightNumber)
          .map(_._1)
      }

      val possibleRunway = {//where the plane currently is
        airport.runways
          .map(r => ((r, r.takeoffQueue.getPlanesInTheQueue)))
          .find(_._2.contains(plane))
          .map(_._1)
      }

      possibleGate match
        case Some(g) if queueToRunway =>
          g.removeIfReady(currentTime)
        case _ => ()

      possibleRunway match
        case Some(r) if queueToRunway =>
          r.takeoffQueue.removeFromQueue(plane)

        case _ if possibleGate.isDefined =>
          ()

        case _ =>
          possibleErrorText = "The plane can't taxi right now"
    end taxiRunway


    def taxiGate(plane: Airplane, gate: Gate) =
      runways.find(_.isWaitingForGate == Some(plane)) match
        case Some(runway) if gate.reserveGate(plane) =>//if is true if theres no plane at the gate
          taxiway.addPlane(currentTime, plane, gate)
          plane.removeCoordinates()
          airport.statistics.addArrival(plane)
          runway.removeWaitingForGate()

        case Some(runway) =>
          possibleErrorText = "Gate is already reserved"

        case _ =>
          possibleErrorText = "No such plane waiting for gate"
      end match
    end taxiGate


    if possibleErrorText.nonEmpty then isInputValid = false
    (isInputValid, possibleErrorText)
  end reactToInput
end InputHandler
