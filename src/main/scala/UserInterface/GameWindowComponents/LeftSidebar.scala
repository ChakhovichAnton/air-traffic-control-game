package UserInterface.GameWindowComponents

import GameLogic.*
import UserInterface.GameWindowComponents.SidebarBackground.*

import scala.swing.*

/**
 * This class builds the left side of the user interface by combining sidebars and
 * adding a border around the sidebars.
 * @param sidebarInfoWidth width of departuresSidebar and arrivalsSidebar. Not combined
 *                          but width of one sidebar
 * @param height The sidebars totalheight
 * @param scrollbarWidth scrollbarwidth
 * @param leftSideBorderWidth width of the border on the left side of the departuresSidebar
 *                            and arrivalsSidebar
 * @param airport the airport this sidebar represents
 */
class LeftSidebar(sidebarInfoWidth   : Int,
                  height             : Int,
                  scrollbarWidth     : Int,
                  leftSideBorderWidth: Int,
                  airport            : Airport,
                  borderHeight       : Int//height of the background color at the top and bottom of the sidebar
                 ):

  private val sidebarInfoHeight = height - 2 * borderHeight//height of the information part of the sidebar
  private val planeCardWidth    = sidebarInfoWidth - scrollbarWidth
  private val roundRectFromEnd  = 5


  private val arrivalsPlaneCards =
    airport.getPlanes.map(PlaneCard(_, planeCardWidth))

  val arrivalsSidebar = Sidebar(
    "ARRIVALS",
    arrivalsPlaneCards,
    sidebarInfoWidth,
    sidebarInfoHeight,
    scrollbarWidth
  )

  private val departuresPlaneCards =
    airport.getPlanes.map(PlaneCard(_, planeCardWidth))

  val departuresSidebar = Sidebar(
    "DEPARTURES",
    departuresPlaneCards,
    sidebarInfoWidth,
    sidebarInfoHeight,
    scrollbarWidth
  )


  private val leftSidebarLeftSide = GrassBackground(leftSideBorderWidth, height)
  private val leftSidebarTop = SidebarEndBackground(
    2 * sidebarInfoWidth + leftSideBorderWidth,
    2 * sidebarInfoWidth,
    borderHeight,
    leftSideBorderWidth,
    borderHeight - roundRectFromEnd
  )

  private val leftSidebarBottom = SidebarEndBackground(
    2 * sidebarInfoWidth + leftSideBorderWidth,
    2 * sidebarInfoWidth,
    borderHeight,
    leftSideBorderWidth,
    -roundRectFromEnd
  )


  private val sidebar = new BorderPanel() {
    add(           leftSidebarTop, BorderPanel.Position.North )
    add(      leftSidebarLeftSide, BorderPanel.Position.West  )
    add(  arrivalsSidebar.sidebar, BorderPanel.Position.Center)
    add(departuresSidebar.sidebar, BorderPanel.Position.East  )
    add(        leftSidebarBottom, BorderPanel.Position.South )
  }

  def getSidebar = sidebar
end LeftSidebar
