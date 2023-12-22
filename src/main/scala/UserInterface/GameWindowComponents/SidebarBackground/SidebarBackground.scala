package UserInterface.GameWindowComponents.SidebarBackground

import UserInterface.Colors.*

import scala.swing.*

trait SidebarBackground:
  def fillBackgroundWithGrass(g: Graphics2D, width: Int, height: Int) = {
    g.setColor(grassColor)
    g.fillRect(0, 0, width, height)
  }

  def drawInformationBoxRounder(g: Graphics2D, startX: Int, startY: Int, width: Int) = {
    g.setColor(componentBackground)
    g.fillRoundRect(startX, startY, width, 10, 10, 10)
  }
end SidebarBackground
