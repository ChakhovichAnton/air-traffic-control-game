package UserInterface.GameWindowComponents

import GameLogic.*
import UserInterface.GameWindowComponents.RunwayCard
import UserInterface.Colors.*

import javax.swing.BorderFactory.createMatteBorder
import scala.swing.*

class RunwayInfo(runwayCards: Vector[RunwayCard]):
  private val heading     = new Label("RUNWAYS")
  private val headingFont = Font("Dialog", Font.Style.Bold, 18)

  heading.font   = headingFont
  heading.border = createMatteBorder(0, 0, 1, 0, blackColor)

  private val maxCardCount = 3
  private val cards        = new BoxPanel(Orientation.Vertical)
  runwayCards.foreach(cards.contents += _.getRunwayCard)

  private val scrollBar    = ScrollBar()
  scrollBar.minimum        = 0
  scrollBar.visibleAmount  = maxCardCount
  scrollBar.maximum        = runwayCards.size
  scrollBar.blockIncrement = 1

  if runwayCards.size > maxCardCount then
    scrollBar.visible = true
  else
    scrollBar.visible = false

  private val window = new BorderPanel {
    add(  heading, BorderPanel.Position.North )
    add(    cards, BorderPanel.Position.Center)
    add(scrollBar, BorderPanel.Position.East  )
  }
  def getRunwayWindow = window

  def updateScrollBar() = {
    val visibleCards =
      runwayCards.slice(scrollBar.value, scrollBar.value + maxCardCount)
    
    runwayCards.filter(!visibleCards.contains(_)).foreach(_.unShow())
    visibleCards.foreach(_.show())
  }

  def updateRunways() =
    runwayCards.foreach(_.updateRunwayCard())
end RunwayInfo
