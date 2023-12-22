package UserInterface.PopUps

import scala.collection.mutable.Buffer
import scala.swing.Swing.HStrut
import scala.swing.event.ButtonClicked
import scala.swing.*

/**
 * Instructions pop-up: "ins" stands for instructions
 */
class InstructionsPopUp(width: Int, height: Int) extends Panel, WindowsPopUp(width, height):
  private val insHeader = new Label("Instructions")
  insHeader.font        = Font(Font.Dialog, Font.Style.Bold, 25)

  private val path = "/Resources/Files/Instructions.txt"
  val labelVector: Vector[Vector[Label]] = {
    FileHandling.FileHandlingHelperMethods.readTextFile(path) match
      case Some(t) =>
        val allText = Buffer[Vector[String]]()
        val currentPageText = Buffer[String]()
        for
          (l, i) <- t.map(n => if n == "" then " " else n).zipWithIndex//Swing labels don't work with "" but they work with " "
        do {
          if l == "<next_page>" then
            allText += currentPageText.toVector
            currentPageText.clear()
          else
            currentPageText += l
        }

        allText += currentPageText.toVector
        allText.toVector.map(_.map(new Label(_)))
      case None =>
        nextPageButton.enabled = false
        Vector(Vector(new Label("Error: Instructions could not be loaded")))
  }


  private val insText = new BoxPanel(Orientation.Vertical) {
    labelVector.foreach(_.foreach(contents += _))
  }
  labelVector.tail.foreach(_.foreach(_.visible = false))

  private var currentInsPopUpPage = 0
  private val instructionsPopUp = new Dialog() {
    contents = new BorderPanel {
      add(insHeader, BorderPanel.Position.North)
      add(insText, BorderPanel.Position.Center)
      add(insButtonPanel, BorderPanel.Position.South)
    }
    title = "Instructions"
    centerOnScreen()
    resizable = false
  }

  this.setPopUpSize()

  def getPopUp = instructionsPopUp

end InstructionsPopUp
