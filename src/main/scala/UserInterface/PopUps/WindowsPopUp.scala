package UserInterface.PopUps

import scala.swing.*
import scala.swing.Swing.HStrut
import scala.swing.event.ButtonClicked
import scala.math.max

/**
 * Trait describes pop-ups which have several pages and buttons
 * for going from one page to an another.
 */
trait WindowsPopUp(width: Int, height: Int) extends Panel:
  private def max(a: Int, b: Int, c: Int) = {
    val maxAB = math.max(a, b)
    math.max(maxAB, c)
  }

  val closeButton        = new Button("Close Pop-up")
  val previousPageButton = new Button("Previous")
  val nextPageButton     = new Button("Next")

  private var currentPopUpPage = 0
  private val buttonCount      = 3
  
  def getCurrentPopUpPage = currentPopUpPage

  //Add five so that swing shows the whole text and doesn't cut out the end
  private val buttonWidth = 5 + max(
    closeButton       .preferredSize.width,
    previousPageButton.preferredSize.width,
    nextPageButton    .preferredSize.width
  )
  private val buttonHeight = max(
    closeButton       .preferredSize.height,
    previousPageButton.preferredSize.height,
    nextPageButton    .preferredSize.height
  )

  private val buttonDimension    = new Dimension(buttonWidth, buttonHeight)
  previousPageButton.enabled     = false
  private val buttonsDistance    =
    (width - buttonCount * buttonWidth) / (buttonCount + 1)
  //The variable above is the distance between the buttons and between the buttons and sides

  //making the buttons the same size
  nextPageButton.preferredSize     = buttonDimension
  nextPageButton.maximumSize       = buttonDimension
  nextPageButton.minimumSize       = buttonDimension
  
  previousPageButton.preferredSize = buttonDimension
  previousPageButton.maximumSize   = buttonDimension
  previousPageButton.minimumSize   = buttonDimension

  closeButton.preferredSize        = buttonDimension
  closeButton.maximumSize          = buttonDimension
  closeButton.minimumSize          = buttonDimension
  
  val insButtonPanel = new BoxPanel(Orientation.Horizontal) {
    contents += HStrut(buttonsDistance)
    contents += previousPageButton
    contents += HStrut(buttonsDistance)
    contents += nextPageButton
    contents += HStrut(buttonsDistance)
    contents += closeButton
  }

  /**
   * The outer vector includes the pages and the inner
   * vector includes the rows.
   */
  val labelVector: Vector[Vector[Label]]

  /**
   * If changeNext is true, change to the next page.
   * Otherwise change to the previous page.
   */
  private def setNextPopUpPage(changeNext: Boolean) =
    for n <- labelVector(currentPopUpPage) do
      n.visible = false
      n.repaint()
    val changeAsInt = if changeNext then 1 else -1
    currentPopUpPage += changeAsInt
    for n <- labelVector(currentPopUpPage) do
      n.visible = true
      n.repaint()
  end setNextPopUpPage

  private def popUpNextPage() =
    if currentPopUpPage < labelVector.length - 1 then
      setNextPopUpPage(true)
      previousPageButton.enabled = true
    if currentPopUpPage >= labelVector.length - 1 then
      nextPageButton.enabled = false
  end popUpNextPage

  private def popUpPreviousPage() =
    if currentPopUpPage > 0 then
      setNextPopUpPage(false)
      nextPageButton.enabled = true
    if currentPopUpPage == 0 then
      previousPageButton.enabled = false
  end popUpPreviousPage
  
  def getPopUp:Dialog
  
  def closePopUp() =
    getPopUp.close()
  
  def openPopUp() =
    getPopUp.pack()//pack method makes the pop-up the correct size
    getPopUp.open()

  /**
   * Method sets up the correct size
   */
  def setPopUpSize() =
    val d = new Dimension(width, height)
    getPopUp.minimumSize   = d
    getPopUp.preferredSize = d
    getPopUp.maximumSize   = d
  end setPopUpSize
  
  
  listenTo(closeButton)
  listenTo(nextPageButton)
  listenTo(previousPageButton)

  reactions += {
    case ButtonClicked(e) if e == closeButton =>
      closePopUp()
    case ButtonClicked(e) if e == nextPageButton =>
      popUpNextPage()
    case ButtonClicked(e) if e == previousPageButton =>
      popUpPreviousPage()
  }
end WindowsPopUp
