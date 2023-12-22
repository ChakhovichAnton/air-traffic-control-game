package GameLogic

/**
 * @param name must start with the letter G
 * @param timeAtTheGate is how much time a plane spends at the gate
 * @param xCoordinate is the gates location's X coordinate on the map
 * @param yCoordinate is the gates location's Y coordinate on the map
 */
class Gate(val name: String, timeAtTheGate: Int, val xCoordinate: Int, val yCoordinate: Int, val gateDir: Direction):
  private var status: Option[(Int, Airplane)] = None
  private var reservationStatus: Option[Airplane] = None

  val width  = 30
  val height = 30

  private def center(airplane: Airplane) = (width - airplane.getWidth)/2

  private def distanceFromEnd(airplane: Airplane) = height - airplane.getHeight + 1

  def planePos(plane: Airplane) =
    gateDir match
      case _: East  =>
        val x = xCoordinate + distanceFromEnd(plane)
        val y = yCoordinate + center(plane)
        (x, y)

      case _: West  =>
        val x = xCoordinate - 1
        val y = yCoordinate + center(plane)
        (x, y)

      case _: South =>
        val x = xCoordinate + center(plane)
        val y = yCoordinate + distanceFromEnd(plane)
        (x, y)

      case _        =>//North
        val x = xCoordinate + center(plane)
        val y = yCoordinate - 1
        (x, y)
  end planePos

  def seeIfEmpty = status.isEmpty && reservationStatus.isEmpty

  def needsReservationText = reservationStatus.isDefined && status.isEmpty
  
  def resetGate() =
    status = None
    reservationStatus = None

  def reserveGate(plane: Airplane): Boolean =
    reservationStatus match
      case Some(plane) =>
        false
      case None =>
        reservationStatus = Some(plane)
        true
  end reserveGate

  def addPlane(plane: Airplane, currentTime: Int): Boolean =
    if
      status.isEmpty              &&
      reservationStatus.isDefined &&
      reservationStatus.get == plane
    then
      status = Some((currentTime + timeAtTheGate, plane))
      plane.setCoordinates(planePos(plane))
      plane.setDirection(gateDir)
      true
    else
      false
  end addPlane

  def removeIfReady(currentTime: Int) =
    this.status match
      case Some((time, plane)) if currentTime >= time =>
        this.status = None
        this.reservationStatus = None
        Some(plane)
      case _ => None
  end removeIfReady

  def getIfReady(currentTime: Int) =
    status match
      case Some((time, plane)) if currentTime >= time =>
        Some(plane)
      case _ =>
        None
  end getIfReady

end Gate
