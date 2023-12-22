package UserInterface.GameWindowComponents

import GameLogic.*
import UserInterface.Colors.*

import javax.swing.BorderFactory.createMatteBorder
import scala.swing.*
import scala.swing.Swing.VStrut

class PlaneCard(val airplane: Airplane, width: Int):
  val height             = 87
  private var visibility = false//could the plane card be visible if the scroll bar is in a favorable position
  private val statusTextBeginning = s"Status: "

  private val planeNumber = new Label(s"Airplane Number: ${airplane.flightNumber}")
  private val passangers  = new Label(s"${airplane.passangers} Passangers")
  private val statusText  = new Label(statusTextBeginning)
  private val minRTakeoff = new Label(s"Min runway length: ${airplane.minimumRunwayLengthForTakeoff} m")
  private val minRLanding = new Label(s"Min runway length: ${airplane.minimumRunwayLengthForLanding} m")

  private val fuelBarPercentage = (100*airplane.getFuel.toDouble / airplane.maxFuel).toInt
  private val fuelBar           = new ProgressBar() {
    max          = 100
    min          = 0
    value        = fuelBarPercentage
    label        = s"${fuelBarPercentage}% fuel left"
    labelPainted = true
  }

  private val planeCard   = new BoxPanel(Orientation.Vertical) {
    contents += planeNumber
    contents += passangers
    contents += statusText
    contents += minRTakeoff
    contents += minRLanding
    contents += fuelBar
    contents += VStrut(1) //needed to make the lower border longer
  }
  
  planeCard.preferredSize = new Dimension(width, height)
  planeCard.maximumSize   = new Dimension(width, height)
  planeCard.minimumSize   = new Dimension(width, height)
  planeCard.border        = createMatteBorder(0, 0, 1, 0, blackColor)
  planeCard.visible       = false

  def getPlaneCard = planeCard

  def isPlaneCardVisible = visibility
  
  def setWidth(newWidth: Int) =
    planeCard.preferredSize = new Dimension(newWidth, height)
    planeCard.maximumSize   = new Dimension(newWidth, height)
    planeCard.minimumSize   = new Dimension(newWidth, height)

  /**
   * The show methods are only for the scroll bar.
   * Rest of them are used to manage the card's status.
   */
  def show() = {
    planeCard.visible = true
    planeCard.repaint()
  }

  def unShow() = {
    planeCard.visible = false
    planeCard.repaint()
  }

  def squish() = {
    visibility        = false
    planeCard.visible = false
    planeCard.repaint()
  }

  def landing() = {
    fuelBar.visible     = true
    minRLanding.visible = true
    minRTakeoff.visible = false
    visibility          = true
    planeCard.repaint()
  }

  def takeoff() = {
    fuelBar.visible     = false
    minRLanding.visible = false
    minRTakeoff.visible = true
    visibility          = true
    planeCard.repaint()
  }

  def updateStatus(newText: String) = {
    statusText.text = statusTextBeginning + newText
    statusText.repaint()
  }

  def updateProgressBar() = {
    val fuelBarPercentage = (100*airplane.getFuel.toDouble / airplane.maxFuel).toInt
    fuelBar.value = fuelBarPercentage
    fuelBar.label = s"${fuelBarPercentage}% Fuel Left"
    fuelBar.repaint()
  }
end PlaneCard
