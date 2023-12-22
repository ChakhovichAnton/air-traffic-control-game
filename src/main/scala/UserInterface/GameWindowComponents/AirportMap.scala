package UserInterface.GameWindowComponents

import GameLogic.*
import UserInterface.Colors.*
import UserInterface.DrawHelper.*
import DrawRunway.*
import DrawTaxiway.*
import DrawGate.drawGate
import FileHandling.FileHandlingHelperMethods.readImage

import java.awt.font.FontRenderContext
import java.awt.image.BufferedImage
import scala.swing.*
import scala.collection.mutable.Buffer

class AirportMap(width   : Int,
                 height  : Int,
                 airport : Airport,
                 taxiways: Vector[(Int, Int, Int, Int)],//Ints: startX, startY, endX, endY
                 surfaces: Vector[(Int, Int, Int, Int, Color)],
                 images  : Vector[(Int, Int, String)]
                ) extends Panel:

  //If message is active the int represents the time. Otherwise false
  //The buffer stores all previously activated weathers. The last one
  //is shown on the screen if weatherMessageActiveTime is defined
  private var weatherMessageActiveTime: Option[Int] = None
  private val alreadyActivatedWeather               = Buffer[Weather]()

  private val runways = airport.runways
  private val gates   = airport.gates

  def onSecond() =//This method is called every second
    weatherMessageActiveTime =
      weatherMessageActiveTime match
        case Some(i) if i > 2 => None
        case Some(i)          => Some(i + 1)
        case None             => None
  end onSecond

  def reset() =
    weatherMessageActiveTime = None
    alreadyActivatedWeather.clear()
  end reset


  private val annoucementTextGap = 20//the width of the gap on the left and
                                     //right side of the annoucement text
  private def getAnnoucementTextFont(f: FontRenderContext, text: String) = {
      def getWidth(text: String, font: Font) = {
        font.getStringBounds(text, f).getBounds.width
      }
      def newFont(size: Int) = Font(Font.Dialog, Font.Style.Bold, size)

      val spaceWidth = width - 2 * annoucementTextGap
      var font       = newFont(80)//a big font so that the text appears clearly
      var textWidth  = getWidth(text, font)

      while textWidth > spaceWidth do
        font      = newFont(font.getSize - 2)
        textWidth = getWidth(text, font)
      end while

      font
    }

  override def paintComponent(g : Graphics2D) =
    super.paintComponent(g)
    g.setColor(grassColor)
    g.fillRect(0, 0, width, height)//Background
    g.setBackground(grassColor)

    for (x1, y1, x2, y2, color) <- surfaces do
      g.setColor(color)
      g.fillRect(x1, y1, x2 - x1, y2 - y1)
    end for

    for (x, y, source) <- images do
      readImage(source) match
        case Some(img) => g.drawImage(img, x, y, null)
        case None      => ()
    end for

    val (allTaxiways, taxiwaysIntersections) = getTaxiwaysAndIntersections

    allTaxiways.foreach(drawTaxiway(g, _, _, _, _))
    runways.foreach(drawRunwayBase(g, _))
    airport.getIntersectingRunways.foreach(drawIntersectingPoints(g, _, _, _, _))
    runways.foreach(drawCenterLine(g, _))
    gates.foreach(drawGate(g, _))

    taxiwaysIntersections.foreach(drawIntersection(g, _, _))
    weatherAnnouncement(g)

    for plane <- airport.getPlanes do
      plane.getCoordinates match
        case Some((x,y)) =>
          readImage(s"Files/${plane.imageSource}") match
            case Some(img) if plane.getDirection.isDefined =>
              val rotatedImg =
                rotateImage(img, plane.getDirection.get.getPlaneRotation.toDouble.toRadians)

              g.drawImage(rotatedImg, x, y, null)
            case _ =>
              g.setColor(redColor)
              g.fillRect(x, y, plane.getWidth, plane.getHeight)
        case None => ()
  end paintComponent


  private def weatherAnnouncement(g: Graphics2D) =
    for r <- runways do
      r.getWeather match
        case Some(w) if !alreadyActivatedWeather.contains(w) =>
          weatherMessageActiveTime = Some(0)
          alreadyActivatedWeather += w
        case _ => ()
    end for

    weatherMessageActiveTime match
      case Some(_) =>
        val annoucementText = {
          alreadyActivatedWeather.lastOption match
            case Some(w) => w.description
            case None    => ""
        }
        
        val announcementFont = getAnnoucementTextFont(g.getFontRenderContext, annoucementText)
        g.setColor(redColor)
        g.setFont(announcementFont)

        val announcementY1 = 200
        val announcementY2 = announcementY1 + announcementFont.getSize + annoucementTextGap
        g.drawString("Caution!", annoucementTextGap, announcementY1)
        g.drawString(annoucementText, annoucementTextGap, announcementY2)
      case _ => ()
    end match
  end weatherAnnouncement


  private def getTaxiwaysAndIntersections =
    def getQueueAreaTaxiway(runway: Runway) =
      val takeoffQueue = runway.takeoffQueue
      if runway.runwayXDirection then
        (
         runway.startX + takeoffQueue.queueAreaFromRunwayStart,
         runway.endY,
         runway.startX + takeoffQueue.queueAreaFromRunwayStart,
         runway.endY   + takeoffQueue.queueAreaLength
        )
      else
        (
         runway.endX,
         runway.startY + takeoffQueue.queueAreaFromRunwayStart,
         runway.endX   + takeoffQueue.queueAreaLength,
         runway.startY + takeoffQueue.queueAreaFromRunwayStart
        )
    end getQueueAreaTaxiway


    val allTaxiways = taxiways ++ runways.map(getQueueAreaTaxiway(_))
    val taxiwaysCC  = allTaxiways.map(TaxiwayGUI(_, _, _, _))

    val taxiwayIntersections:Vector[(Int, Int)] = {
      taxiwaysCC.combinations(2).toVector
        .map(t => (t.head, t(1)))
        .map((t1, t2) => t1.findIntersection(t2))
        .filter(_.isDefined)
        .map(_.get)
    }

    def getTaxiwaysAtCoords(x: Int, y: Int): Vector[TaxiwayGUI] = {
      taxiwaysCC
        .filter(t => (t.startX to t.endX).contains(x) &&
          (t.startY to t.endY).contains(y)
        )
    }

    val taxiwayIntersectionMap: Map[(Int, Int), Vector[TaxiwayGUI]] = {
      taxiwayIntersections
        .map(n => (n, getTaxiwaysAtCoords(n._1, n._2))).toMap
    }

    (allTaxiways, taxiwayIntersectionMap)
  end getTaxiwaysAndIntersections

end AirportMap
