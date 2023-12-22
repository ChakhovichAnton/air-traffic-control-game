package UserInterface.GameWindowComponents

import GameLogic.Runway
import UserInterface.DrawHelper.drawRotatedText
import UserInterface.Colors.*

import scala.swing.*

/**
 * An object which draws runways. To get the optimal result, call each
 * function for all runways at a time in the follwing order:
 * 1. drawRunwayBase
 * 2. drawIntersectingPoints
 * 3. drawCenterLine
 */
object DrawRunway:
  private val runwayPavementColor = blackColor
  private val runwayMarkingColor  = whiteColor

  private val runwaySideWidth               = 4
  private val runwayThresholdLength         = 8
  private val runwayThresholdRectanlgeWidth = 2
  private val thresholdEndGap               = 1
  private val nameDistanceFromEnd           = 11

  def drawRunwayBase(g: Graphics2D, runway: Runway) =
    def drawRunwayThresholdX(runwayWidth: Int, xCoordinate: Int, yCoordinate: Int) = {
      g.setColor(runwayMarkingColor)
      for i <- 2 to runwayWidth - 4 do
        if (i)%4 == 2 then
          g.fillRect(xCoordinate, yCoordinate + i, runwayThresholdLength, runwayThresholdRectanlgeWidth)
    }
    def drawRunwayThresholdY(runwayWidth: Int, xCoordinate: Int, yCoordinate: Int) = {
      g.setColor(runwayMarkingColor)
      for i <- 2 to runwayWidth - 4 do
        if (i)%4 == 2 then
          g.fillRect(xCoordinate + i, yCoordinate, runwayThresholdRectanlgeWidth, runwayThresholdLength)
    }

    val x = runway.startX
    val y = runway.startY
    val w = runway.width
    val l = runway.length

    val displayedName = runway.name.tail
    val isXDir        = runway.runwayXDirection

    val xDistance = if isXDir then l else w
    val yDistance = if isXDir then w else l

    g.setColor(runwayPavementColor)
    g.fillRect(x - runwaySideWidth, y - runwaySideWidth, xDistance + 7, yDistance + 7)
    g.setColor(runwayMarkingColor)
    g.drawRect(x - 1, y - 1, xDistance + 1, yDistance + 1)

    if isXDir then
      //Runway threshold markings
      drawRunwayThresholdX(runway.width, x + thresholdEndGap, y)
      drawRunwayThresholdX(runway.width, runway.endX - runwayThresholdLength - thresholdEndGap, y)
      //Runway name text
      drawRotatedText(g, displayedName, 90 , x + nameDistanceFromEnd          , y + 7          )
      drawRotatedText(g, displayedName, 270, runway.endX - nameDistanceFromEnd, runway.endY - 9)
    else
      //Runway threshold markings
      drawRunwayThresholdY(runway.width, x, y + thresholdEndGap)
      drawRunwayThresholdY(runway.width, x, runway.endY - runwayThresholdLength - thresholdEndGap)
      //Runway name text
      drawRotatedText(g, displayedName, 180, x + 20, runway.startY + nameDistanceFromEnd)
      drawRotatedText(g, displayedName, 0  , x + 7 , runway.endY - nameDistanceFromEnd  )
  end drawRunwayBase

  def drawIntersectingPoints(g: Graphics2D, xDirRunway: Runway, yDirRunway: Runway, pointX:Int, pointY: Int) =
    val w = xDirRunway.width //runway width: 30 (all runways are of the same width)

    g.setColor(runwayPavementColor)
    g.drawLine(pointX - 1, pointY - 1, pointX + w, pointY - 1)

    g.setColor(runwayMarkingColor)
    g.drawLine(pointX - 1, pointY - 1, pointX - 1, pointY - runwaySideWidth)
    g.drawLine(pointX + w, pointY - 1, pointX + w, pointY - runwaySideWidth)

    g.setColor(runwayPavementColor)
    g.drawLine(pointX - 1, pointY + w, pointX + w, pointY + w)

    g.setColor(runwayMarkingColor)
    g.drawLine(pointX - 1, pointY + w, pointX - 1, pointY + runwaySideWidth + 30)
    g.drawLine(pointX + w, pointY + w, pointX + w, pointY + runwaySideWidth + 30)
  end drawIntersectingPoints


  def drawCenterLine(g: Graphics2D, runway: Runway) =
    g.setColor(runwayMarkingColor)
    val distanceForCenterLine =
      runway.length - 2 * nameDistanceFromEnd - 2 * g.getFont.getSize

    val firstXCoordinate =
      if runway.runwayXDirection then
        runway.startX + nameDistanceFromEnd + g.getFont.getSize
      else
        runway.startX + 14

    val firstYCoordinate =
      if runway.runwayXDirection then
        runway.startY + 14
      else
        runway.startY + nameDistanceFromEnd + g.getFont.getSize

    var count = 0
    for i <- 0 until (distanceForCenterLine.toDouble/20.0).ceil.toInt do
      g.fillRect(
        if runway.runwayXDirection then firstXCoordinate + count else firstXCoordinate,
        if runway.runwayXDirection then firstYCoordinate else firstYCoordinate + count,
        if runway.runwayXDirection then 10 else 1,
        if runway.runwayXDirection then 1 else 10
      )
      count += 20
  end drawCenterLine

end DrawRunway
