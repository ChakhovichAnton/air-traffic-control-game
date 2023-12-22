package UserInterface.GameWindowComponents

import UserInterface.Colors.*
import GameLogic.*

import scala.swing.*

object DrawGate:
  private val reservationTextFont = Font(Font.Dialog, Font.Style.Plain, 6)
  private val gateNameFont        = Font(Font.Dialog, Font.Style.Plain, 9)
  
  def drawGate(g: Graphics2D, gate: Gate) =
    def drawReservationText(x: Int, y: Int, gate: Gate) = {
      if gate.needsReservationText then
        g.setColor(redColor)
        g.setFont(reservationTextFont)
        g.drawString("Reserved", x, y)
    }
    
    val height = gate.height//30
    val width  = gate.width //30
    val ld     = 20 //label location distance

    val x = gate.xCoordinate
    val y = gate.yCoordinate

    g.setFont(gateNameFont)
    g.setColor(pavementColor)

    gate.gateDir match
      case w: West =>
        g.fillRect(x - ld, y               , width + ld, height          )
        g.setColor(yellowGroundMarking)
        g.drawLine(x     , height/2 + y    , x + 30    , height/2 + y    )
        g.drawLine(x     , height/2 + y + 2, x         , height/2 + y - 2)
        g.drawString(gate.name             , x - 19    , height/2 + y + 4)
        drawReservationText(x + 1, y + 15, gate)

      case s: South =>
        g.fillRect(x               , y     , width           , height + ld)
        g.setColor(yellowGroundMarking)
        g.drawLine(x + height/2    , y     , x + height/2    , y + 30     )
        g.drawLine(x + height/2 + 2, y + 30, x + height/2 - 2, y + 30     )
        g.drawString(gate.name             , x + 9           , y + 45     )
        drawReservationText(x + 1, y + height/2, gate)

      case e: East =>
        g.fillRect(x     , y               , width + ld, height          )
        g.setColor(yellowGroundMarking)
        g.drawLine(x     , height/2 + y    , x + 30    , height/2 + y    )
        g.drawLine(x + 30, height/2 + y + 2, x + 30    , height/2 + y - 2)
        g.drawString(gate.name             , x + 39    , height/2 + y + 4)
        drawReservationText(x + 1, y + height/2, gate)

      case _ => //North
        g.fillRect(x               , y - ld, width           , height + ld)
        g.setColor(yellowGroundMarking)
        g.drawLine(x + height/2    , y     , x + height/2    , y + 30     )
        g.drawLine(x + height/2 + 2, y     , x + height/2 - 2, y          )
        g.drawString(gate.name             , x + height/2 - 5, y - 9      )
        drawReservationText(x + 1, y + height/2, gate)
    end match
  end drawGate
end DrawGate
