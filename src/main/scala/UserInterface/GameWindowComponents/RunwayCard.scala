package UserInterface.GameWindowComponents

import GameLogic.*

import java.awt.Color
import javax.swing.BorderFactory.createMatteBorder
import scala.swing.*
import scala.swing.Swing.VStrut

class RunwayCard(val runway: Runway, airport: Airport, width: Int):
  private val waitStatusGatePrefix = "Waiting For Gate: "
  private val queueStatusPrefix    = "Queue: "
  private val weatherPrefix        = "Weather: "
  private val empty                = "Empty"
  private val textHeight           = 15

  private val nameLabel      = new Label(s"Runway: ${runway.name} (Length: ${runway.length} m)")
  private val waitStatusGate = new Label(waitStatusGatePrefix + empty)
  private val queueStatus    = new Label(queueStatusPrefix    + empty)
  private val weather        = new Label(weatherPrefix        + empty)

  private val card = new BoxPanel(Orientation.Vertical) {
    contents += nameLabel
    contents += waitStatusGate
    contents += queueStatus
    contents += weather
    contents += VStrut(1) //needed to make the lower border longer
    border    = createMatteBorder(0, 0, 1, 0, Color.BLACK)
    visible   = false
  }

  def getRunwayCard = card

  def show() =
    card.visible = true
    card.repaint()

  def unShow() =
    card.visible = false
    card.repaint()


  def updateRunwayCard() =
    waitStatusGate.text = runway.isWaitingForGate match
      case Some(plane) =>
        waitStatusGatePrefix + plane.flightNumber
      case None =>
        waitStatusGatePrefix + empty

    weather.text = weatherPrefix +
      (runway.getWeatherDescription match
         case Some(description) => "Closed: " + description
         case None              => empty
      )

    var queueText = queueStatusPrefix
    for
      planeNumber <- runway.takeoffQueue.getPlanesInTheQueue.map(_.flightNumber)
    do
      queueText = queueText + planeNumber + " "
    
    for
      planeNumber <- airport.taxiways.getPlanesTaxiingTo(runway).map(_.flightNumber)
    do
      queueText = queueText + "T" + planeNumber + " "
      
    queueStatus.text =
      if queueText.length == 7 then
        queueStatusPrefix + empty
      else
        queueText

    waitStatusGate.repaint()
    queueStatus.repaint()
  end updateRunwayCard

end RunwayCard
