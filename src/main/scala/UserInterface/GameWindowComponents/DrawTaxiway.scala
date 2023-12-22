package UserInterface.GameWindowComponents

import UserInterface.Colors.*
import GameLogic.*
import UserInterface.DrawHelper.*
import TaxiwayCurves.*

import scala.swing.*
import scala.math.{max, min, Pi}

object DrawTaxiway:
  case class TaxiwayGUI(startX: Int, startY: Int, endX: Int, endY: Int) {

    def findIntersection(taxiway: TaxiwayGUI): Option[(Int, Int)] =
      val intersectionX = (startX to endX).intersect(taxiway.startX to taxiway.endX)
      val intersectionY = (startY to endY).intersect(taxiway.startY to taxiway.endY)

      if intersectionX.length == 1 && intersectionY.length == 1 then
        Some((intersectionX.head, intersectionY.head))
      else
        None
  }

  private val w                      = curveImageWidth//16 (shortens code)
  private val taxiwayWidthOnEachSide = 9
  private val taxiwayShoulderWidth   = 2
  private val taxiwayWidth           =
    2 * taxiwayWidthOnEachSide + 3 + 2 * taxiwayShoulderWidth


  private def drawCurve(g: Graphics2D,
                        x: Int,
                        y: Int,
                        dirs: Vector[Direction]//size is 2
                       ) =
    val dirsToImg = Map(
      Vector(East(), South()) -> rotateImage(northToWest, Pi),
      Vector(South(), West()) -> rotateImage(northToWest, 3*Pi/2),
      Vector(West(), North()) -> northToWest,
      Vector(East(), North()) -> rotateImage(northToWest, Pi/2)
    )

    def extendLine(direction: Direction) = {
      direction match
        case _: East  => g.drawLine(x + w, y    , x + w/2 + 1, y          )
        case _: West  => g.drawLine(x - w, y    , x - w/2 - 1, y          )
        case _: South => g.drawLine(x    , y + w, x          , y + w/2 + 1)
        case _        => g.drawLine(x    , y - w, x          , y - w/2 - 1)
    }

    val sortedDirs = dirs.sortBy(_.getPlaneRotation)
    g.drawImage(dirsToImg(sortedDirs), x - w, y - w, null)
    g.setColor(yellowGroundMarking)
    dirs.foreach(extendLine(_))
  end drawCurve


  private def draw3WayIntersection(g: Graphics2D,
                                   xCoordinate: Int,
                                   yCoordinate: Int,
                                   directions: Vector[Direction]//size is 3
                                  ) =
    val allDirections   = Vector[Direction](East(), West(), North(), South())
    val missingDir      = allDirections.filterNot(directions.contains(_)).head
    val intersectionEnd = 2 * w + 1

    val x = xCoordinate - w
    val y = yCoordinate - w

    g.translate(x, y)
    g.setColor(pavementColor)
    missingDir match //these can't be rotated due to the central line widening
      case _: West  =>
        g.drawImage(getCurveImage(true, true), w, 0, null)
        g.drawImage(getCurveImage(false, true), w, w, null)
        g.drawLine(w, w, w + 8, w)
        g.setColor(yellowGroundMarking)
        g.drawLine(w, 0, w, intersectionEnd)

      case _: East  =>
        g.drawImage(getCurveImage(true, false), 0, 0, null)
        g.drawImage(getCurveImage(false, false), 0, w, null)
        g.drawLine(w, w, w - 8, w)
        g.setColor(yellowGroundMarking)
        g.drawLine(w, 0, w, intersectionEnd)

      case _: North =>
        g.drawImage(getCurveImage(false, false), 0, w, null)
        g.drawImage(getCurveImage(false, true), w, w, null)
        g.drawLine(w, w, w, w + 8)
        g.setColor(yellowGroundMarking)
        g.drawLine(0, w, intersectionEnd, w)

      case _: South =>
        g.drawImage(getCurveImage(true, false), 0, 0, null)
        g.drawImage(getCurveImage(true, true), w, 0, null)
        g.drawLine(w, w, w, w - 8)
        g.setColor(yellowGroundMarking)
        g.drawLine(0, w, intersectionEnd, w)
    end match
    g.translate(-x, -y)
  end draw3WayIntersection

  private def draw4WayIntersection(g: Graphics2D, x: Int, y: Int) =
    g.setColor(yellowGroundMarking)
    g.drawLine(x - w, y    , x + w, y    )
    g.drawLine(x    , y - w, x    , y + w)

    g.drawImage(getCurveImage(true , false), x - w, y - w, null)
    g.drawImage(getCurveImage(true ,  true), x    , y - w, null)
    g.drawImage(getCurveImage(false, false), x - w, y    , null)
    g.drawImage(getCurveImage(false,  true), x    , y    , null)
  end draw4WayIntersection


  private def findDirections(intersection: (Int, Int), taxiways: Vector[TaxiwayGUI]) =
    def findDirection(intersection: (Int, Int), taxiway: TaxiwayGUI) =
      var dir2: Option[Direction] = None
      val dir1 = {
        if
          (taxiway.startX == intersection._1 || taxiway.endX == intersection._1) &&
            (taxiway.startY == intersection._2 || taxiway.endY == intersection._2)
        then
          if max(taxiway.startX, taxiway.endX) > intersection._1 then East()
          else if min(taxiway.startX, taxiway.endX) < intersection._1 then West()
          else if max(taxiway.startY, taxiway.endY) > intersection._2 then South()
          else North()
        else if taxiway.startX == intersection._1 then
          dir2 = Some(South())
          North()
        else
          dir2 = Some(East())
          West()
      }
      (dir1, dir2)
    end findDirection

    val foundDirs = taxiways.map(findDirection(intersection, _))
    val dir1s = foundDirs.map(_._1)
    val dir2s = foundDirs.map(_._2).filter(_.isDefined).map(_.get)
    (dir1s ++ dir2s).distinct
  end findDirections


  /**
   * Method can only draw vertical and horizontal taxiways
   * All coordinates are coordinates of the central line
   */
  def drawTaxiway(g: Graphics2D,
                  startX: Int,
                  startY: Int,
                  endX: Int,
                  endY: Int
                 ) =
    def leftSideCoord(coordinate: Int) =
      coordinate - taxiwayWidthOnEachSide - 1 - taxiwayShoulderWidth

    g.setColor(pavementColor)
    if startX == endX then//is vectical
      g.fillRect(leftSideCoord(startX), startY, taxiwayWidth, endY - startY)
      g.setColor(yellowGroundMarking)
      g.drawLine(startX - taxiwayWidthOnEachSide, startY, endX - taxiwayWidthOnEachSide, endY)
      g.drawLine(startX + taxiwayWidthOnEachSide, startY, endX + taxiwayWidthOnEachSide, endY)
    else//is horizontal
      g.fillRect(startX, leftSideCoord(startY), endX - startX, taxiwayWidth)
      g.setColor(yellowGroundMarking)
      g.drawLine(startX, startY - taxiwayWidthOnEachSide, endX, startY - taxiwayWidthOnEachSide)
      g.drawLine(startX, startY + taxiwayWidthOnEachSide, endX, startY + taxiwayWidthOnEachSide)
    g.drawLine(startX, startY, endX, endY)//center line
  end drawTaxiway


  def drawIntersection(g: Graphics2D, intersection: (Int, Int), taxiways: Vector[TaxiwayGUI]) =
    val directions = findDirections(intersection, taxiways)
    val x          = intersection._1
    val y          = intersection._2

    if directions.size == 2 then
      drawCurve(g, x, y, directions)
    else if directions.size == 3 then
      draw3WayIntersection(g, x, y, directions)
    else if directions.size == 4 then
      draw4WayIntersection(g, x, y)
  end drawIntersection
end DrawTaxiway
