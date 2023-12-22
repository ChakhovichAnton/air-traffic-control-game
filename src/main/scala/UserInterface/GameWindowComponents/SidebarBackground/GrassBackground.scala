package UserInterface.GameWindowComponents.SidebarBackground

import scala.swing.*

class GrassBackground(width: Int, height: Int) extends Panel, SidebarBackground:
    this.preferredSize = new Dimension(width, height)

    override def paintComponent(g: Graphics2D): Unit =
      super.paintComponent(g)
      fillBackgroundWithGrass(g, width, height)
end GrassBackground
