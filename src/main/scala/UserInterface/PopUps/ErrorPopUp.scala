package UserInterface.PopUps

import scala.swing.*
import scala.swing.Swing.HStrut
import scala.swing.event.ButtonClicked

class ErrorPopUp(message: String, width: Int, height: Int) extends Panel:
  private val errorPopUpDimensions = new Dimension(width, height)
  private val closeErrorButton     = new Button("Close")

  private val errorPopUp = new Dialog() {
    contents = new BorderPanel {
      add(
        new BoxPanel(Orientation.Vertical) {
          contents += new Label("Oops, something went wrong!")
          contents += new Label(message)
        },
        BorderPanel.Position.Center
      )
      add(
        new BoxPanel(Orientation.Horizontal) {
          contents += HStrut((width - closeErrorButton.preferredSize.width - 17)/2)//-17 is needed for correcting the distance
          contents += closeErrorButton
        },
        BorderPanel.Position.South
      )
    }
    title = "Error"
    centerOnScreen()
    resizable = false
  }

  errorPopUp.minimumSize   = errorPopUpDimensions
  errorPopUp.preferredSize = errorPopUpDimensions
  errorPopUp.maximumSize   = errorPopUpDimensions

  def getPopUp = errorPopUp

  listenTo(closeErrorButton)

  reactions += {
    case ButtonClicked(e) if e == closeErrorButton =>
      errorPopUp.close()
  }
end ErrorPopUp
