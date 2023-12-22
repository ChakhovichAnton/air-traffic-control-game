package UserInterface.PopUps

import FileHandling.FileHandlingHelperMethods.*

import scala.swing.*
import scala.swing.event.MouseWheelMoved
import scala.swing.Swing.HStrut
import scala.collection.mutable.Buffer

class GameReportPopUp(width: Int, height: Int) extends Panel, WindowsPopUp(width, height):
  private val maxCharCountOnLine = 80
  private val maxLabelCount = 13
  //The amount of labels that can appear on one page at the same time vertically
  
  private val header             = new Label("Game Reports")
  private val gameReportSource   = "gameReports"
  private val gameReportFileList = getFileList(gameReportSource, ".txt")

  private val files = gameReportFileList.map(s => (s, readTextFile(s)))

  val scrollBar            = ScrollBar()
  scrollBar.minimum        = 0
  scrollBar.visibleAmount  = maxLabelCount
  scrollBar.maximum        = maxLabelCount
  scrollBar.blockIncrement = 1
  scrollBar.visible        = false

  /**
   * Method which returns a string vector where all of the empty strings ("")
   * have been changed to " ".
   * This method is needed since swing labels can't distinguish empty strings
   */
  private def correctEmptyStrings(v: Vector[String]) =
    for
      s <- v
    yield
      if s == "" then " " else s
  end correctEmptyStrings


  /**
   * A text formatter which splits lines of text into lines of text
   * if the line's char count is larger than maxCharCountOnLine
   */
  private def formatTextIfTooLong(stringVector: Vector[String]) =
    val newTextBuffer = Buffer[String]()
    for
      s <- stringVector
    do
      var currentString = s
      while currentString.length > maxCharCountOnLine do
        val takingWholeLine = currentString.take(maxCharCountOnLine)
        val i = {
          val i = takingWholeLine.lastIndexWhere(_ == ' ')
          if i == -1 then takingWholeLine.length - 1 else i
        }
        newTextBuffer += takingWholeLine.take(i)
        currentString = currentString.drop(i + 1)
      newTextBuffer += currentString
    end for

    newTextBuffer.toVector
  end formatTextIfTooLong


  private val pages = {
    for
      (source, file) <- files
    yield
      file match
        case Some(t) =>
          formatTextIfTooLong(correctEmptyStrings(t.toVector))
            .map(new Label(_))
        case t       =>
          Vector(
            new Label("Unable to load game report from file:"),
            new Label(source)
          )
  }


  val labelVector: Vector[Vector[Label]] = {
    if pages.isEmpty then
      nextPageButton.enabled = false
      Vector(Vector(new Label("No game reports!")))
    else if pages.length == 1 then
      nextPageButton.enabled = false
      pages
    else
      nextPageButton.enabled = true
      pages
  }
  labelVector.tail.foreach(_.foreach(_.visible = false))

  private val reportText = new BoxPanel(Orientation.Vertical) {
    labelVector.foreach(_.foreach(contents += _))
  }

  private val popUpLayout = new BorderPanel {
    add(        header, BorderPanel.Position.North )
    add(     HStrut(5), BorderPanel.Position.West  )
    add(    reportText, BorderPanel.Position.Center)
    add(     scrollBar, BorderPanel.Position.East  )
    add(insButtonPanel, BorderPanel.Position.South )
  }

  private val gameReportPopUp = new Dialog() {
    contents = popUpLayout
    title = "Game Reports"
    centerOnScreen()
    resizable = false
  }

  this.setPopUpSize()


  def updateScrollBar() =
    val page = labelVector(getCurrentPopUpPage)
    val v    = scrollBar.value

    if page.length > maxLabelCount then
      scrollBar.visible = true
      scrollBar.maximum = page.length

      val visibleLabels = page.slice(v, v + maxLabelCount)

      page.filter(!visibleLabels.contains(_)).foreach(_.visible = false)
      visibleLabels.foreach(_.visible = true)
    else
      scrollBar.visible = false
  end updateScrollBar


  /**
   * Method takes a mouse wheel event and changes the value
   * of the scrollbar depending on the event. This only
   * changed the vertical scrollbar.
   */
  def mouseWheelMoved(e: MouseWheelMoved) =
    val v   = scrollBar.value
    val max = scrollBar.maximum
    val min = scrollBar.minimum
    val rot = e.rotation

    if rot + v >= max then
      scrollBar.value = scrollBar.maximum
    else if rot + v <= min then
      scrollBar.value = scrollBar.minimum
    else
      scrollBar.value = scrollBar.value + rot
  end mouseWheelMoved


  def getPopUp: Dialog = gameReportPopUp


  listenTo(popUpLayout.mouse.wheel)

  reactions += {
    case e:MouseWheelMoved => mouseWheelMoved(e)
  }
end GameReportPopUp
