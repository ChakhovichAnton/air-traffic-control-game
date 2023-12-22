package UserInterface.CustomButton

import java.awt.image.BufferedImage
import scala.swing.*

//Class for creating custom buttons

/**
 * Class for creating custom buttons. The listeners should be set up
 * there, where an instance of the class is used.
 * @param text or label of the button. The method doesn't check that
 *             the text fits in the button.
 * @param width width of the button
 * @param height height of the button
 * @param textColor text color
 * @param textFont font of the text
 * @param buttonColor the color of the button when mouse is not on top
 *                    of it
 * @param buttonColorWithMouse the color of the button when mouse is
 *                              on top of it
 * @param borderColor the color of the button border
 */
class RectangleButton(text: String,
                      xCoordinate: Int,
                      yCoordinate: Int,
                      width: Int,
                      height: Int,
                      textColor: Color,
                      textFont: Font,
                      buttonColor: Color,
                      buttonColorWithMouse: Option[Color],
                      borderColor: Option[Color]
                     ) extends CustomButton(xCoordinate,
                                            yCoordinate,
                                            width,
                                            height
                                           ):

  /**
   * Method which draws the button specified above.
   * @param g is the graphics, the button should be drawn on
   */
  def drawButton(g : Graphics2D) =
    val currentButtonColor = if mouseIsOnButton then
      buttonColorWithMouse match
        case Some(c) => c
        case None => buttonColor
    else
      buttonColor
    g.setColor(currentButtonColor)
    g.fillRoundRect(xCoordinate,//drawing the button
                    yCoordinate,
                    width,
                    height,
                    rectangleArcLength,
                    rectangleArcLength
                   )
    borderColor match
      case Some(c) =>
        g.setColor(c)
        g.drawRoundRect(xCoordinate,//drawing the button border
                        yCoordinate,
                        width,
                        height,
                        rectangleArcLength,
                        rectangleArcLength
                       )
      case None => ()
    g.setFont(textFont)
    g.setColor(textColor)
    val textBounds = textFont.getStringBounds(text, g.getFontRenderContext).getBounds
    g.drawString(text,
                 xCoordinate + (width - textBounds.width)/2,
                 yCoordinate + (height - textBounds.height)/2 + textBounds.height - 10
                )
  end drawButton

end RectangleButton
