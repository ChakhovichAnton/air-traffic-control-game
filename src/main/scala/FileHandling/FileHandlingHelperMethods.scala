package FileHandling

import scala.util.{Failure, Success, Try}
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.*

object FileHandlingHelperMethods:
  /**
   *
   * @param directory the directory from which the files will be listed
   * @param filenameExtension for example ".json" in which case the method only returns the json files.
   *                          If it's an empty string "", then the method returns all files.
   * @return the files in the directory specified. Return no files if there are for some reason files with no names or someting went wrong.
   */
  def getFileList(directory: String, filenameExtension: String): Vector[String] = {
    val d = new File(directory)
    d.listFiles()
      .toVector
      .map(_.toString)
      .filter(_.endsWith(filenameExtension))
  }

  def readTextFile(source: String):Option[Iterator[String]] = {
    Try(scala.io.Source.fromFile(source)) match
      case Success(s) => Some(s.getLines())
      case Failure(e) => None
  }


  def readImage(source: String):Option[BufferedImage] = {
    val f = new File(source)
    
    Try(ImageIO.read(f)) match
      case Failure(e) =>
        None
      case Success(bufferedImage) =>
        Some(bufferedImage)
  }
end FileHandlingHelperMethods
