package UserInterface.GameWindowComponents

import GameLogic.*
import UserInterface.Colors.*

import javax.swing.BorderFactory.createMatteBorder
import javax.swing.Timer
import javax.swing.text.Position
import scala.swing.*
import scala.swing.Swing.ActionListener

class Sidebar(header        : String,
              val planeCards: Vector[PlaneCard],
              width         : Int,
              height        : Int,
              scrollbarWidth: Int
             ) extends Panel:

  private val sidebarDimension = new Dimension(width, height)

  private val cards = new BoxPanel(Orientation.Vertical)
  private val label = new Label(header)
  label.border      = createMatteBorder(0, 0, 1, 0, blackColor)
  label.font        = Font("Dialog", Font.Style.Bold, 18)

  private val noPlanesLabel    = new Label("No Planes")
  private val noPlanesLabelDim = new Dimension(width, 10)
  noPlanesLabel.minimumSize    = noPlanesLabelDim
  noPlanesLabel.preferredSize  = noPlanesLabelDim
  noPlanesLabel.visible        = false

  cards.contents += noPlanesLabel
  planeCards.foreach(cards.contents += _.getPlaneCard)
  cards.border = createMatteBorder(0, 1, 1, 0, blackColor)

  private val maxCardCount       = 6//how many plane cards can be visible at a same time on a sidebar
  private val scrollBarHeight    = 523
  private val scrollBarDimension = new Dimension(scrollbarWidth, scrollBarHeight)

  private val scrollBar    = ScrollBar()
  scrollBar.minimum        = 0
  scrollBar.visibleAmount  = maxCardCount
  scrollBar.maximum        = maxCardCount
  scrollBar.blockIncrement = 1
  
  val sidebar = new BorderPanel {
    add(    label, BorderPanel.Position.North )
    add(    cards, BorderPanel.Position.Center)
    add(scrollBar, BorderPanel.Position.East  )
  }

  scrollBar.maximumSize   = scrollBarDimension
  scrollBar.preferredSize = scrollBarDimension
  scrollBar.minimumSize   = scrollBarDimension

  sidebar.maximumSize   = sidebarDimension
  sidebar.preferredSize = sidebarDimension
  sidebar.minimumSize   = sidebarDimension

  def moveScrollBar(amount: Int) =
    val v = scrollBar.value
    
    if amount + v >= scrollBar.maximum then
      scrollBar.value = scrollBar.maximum
    else if amount + v <= scrollBar.minimum then
      scrollBar.value = scrollBar.minimum
    else
      scrollBar.value = v + amount
  end moveScrollBar

  def updateScrollBar() = {
    val visiblePlanes     = planeCards.filter(_.isPlaneCardVisible)
    val visiblePlanesSize = visiblePlanes.size

    if visiblePlanesSize > maxCardCount then
      scrollBar.visible = true
      scrollBar.maximum = visiblePlanesSize
      val visibleCards  = visiblePlanes.slice(scrollBar.value, scrollBar.value + maxCardCount)

      visiblePlanes.filter(!visibleCards.contains(_)).foreach(_.unShow())
      visibleCards.foreach(_.show())

      val newPlaneCardWidth = width - scrollbarWidth
      visiblePlanes.foreach(_.setWidth(newPlaneCardWidth))
    else
      visiblePlanes.foreach(_.show())
      scrollBar.visible = false
      visiblePlanes.foreach(_.setWidth(width))
  }


  def updateSidebarArrivals(
    taxiing       : Vector[Airplane],
    air           : Vector[Airplane],
    landing       : Vector[Airplane],
    waitingForGate: Vector[Airplane]
  ) =
    
    for plane <- taxiing do
      planeCards.find(plane == _.airplane) match
        case Some(i) => i.squish()
        case None => ()

    for plane <- air do
      planeCards.find(plane == _.airplane) match
        case Some(i) =>
          i.landing()
          i.updateProgressBar()
          i.updateStatus("In The Air")
        case None => ()

    for plane <- landing do
      planeCards.find(plane == _.airplane) match
        case Some(i) =>
          i.landing()
          i.updateStatus("Landing")
        case None => ()

    for plane <- waitingForGate do
      planeCards.find(plane == _.airplane) match
        case Some(i) =>
          i.landing()
          i.updateStatus("Waiting For Gate")
        case None => ()

    if planeCards.isEmpty || planeCards.forall(!_.isPlaneCardVisible) then
      noPlanesLabel.visible = true
    else
      noPlanesLabel.visible = false
  end updateSidebarArrivals

  def updateSidebarDepartures(
    done   : Vector[Airplane],
    atGate : Vector[Airplane],
    taxiing: Vector[Airplane],
    queue  : Vector[Airplane],
    takeoff: Vector[Airplane]
  ) =
    for plane <- done do
      planeCards.find(plane == _.airplane) match
        case Some(i) => i.squish()
        case None => ()

    for plane <- atGate do
      planeCards.find(plane == _.airplane) match
        case Some(i) =>
          i.takeoff()
          i.updateProgressBar()
          i.updateStatus("Waiting For Runway")
        case None => ()
        
    for plane <- taxiing do
      planeCards.find(plane == _.airplane) match
        case Some(i) =>
          i.takeoff()
          i.updateStatus("Taxiing To Runway")
        case None => ()

    for plane <- queue do
      planeCards.find(plane == _.airplane) match
        case Some(i) =>
          i.takeoff()
          i.updateStatus("Queueing")
        case None => ()

    for plane <- takeoff do
      planeCards.find(plane == _.airplane) match
        case Some(i) =>
          i.takeoff()
          i.updateStatus("Takeoff")
        case None => ()

    if planeCards.isEmpty || planeCards.forall(!_.isPlaneCardVisible) then
      noPlanesLabel.visible = true
    else
      noPlanesLabel.visible = false
  end updateSidebarDepartures
end Sidebar
