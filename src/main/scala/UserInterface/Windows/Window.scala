package UserInterface.Windows

import UserInterface.WindowManager

import scala.swing.Color

trait Window():
  val windowManager = WindowManager

  //Dimensions of components
  val (
    leftSidebarWidth,  //155
    rightSidebarWidth, //155
    mapWidth,          //600
    mapHeight,         //600
    frameHeight,       //635
    frameWidth,        //1120
    scrollbarWidth,    //17
    leftSideBorderWidth//10
    ): (Int, Int, Int, Int, Int, Int, Int, Int) = windowManager.getDimensions

  def openWindow(): Unit

  def closeWindow(): Unit

end Window
