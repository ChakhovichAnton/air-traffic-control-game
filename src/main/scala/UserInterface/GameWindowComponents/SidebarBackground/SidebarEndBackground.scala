package UserInterface.GameWindowComponents.SidebarBackground

import scala.swing.*

/**
 * Used to generate a background for the top and bottom of a sidebar
 */
class SidebarEndBackground(width             : Int,
                           sidebarWidth      : Int,
                           height            : Int,
                           sidebarFromTheLeft: Int,
                           sidebarFromTheTop : Int
                          ) extends Panel, SidebarBackground:

  this.preferredSize = new Dimension(width, height)

  override def paintComponent(g: Graphics2D): Unit =
    super.paintComponent(g)

    fillBackgroundWithGrass(g, width, height)
    drawInformationBoxRounder(g, sidebarFromTheLeft, sidebarFromTheTop, sidebarWidth)
end SidebarEndBackground
