package FileHandling

import scala.util.{Failure, Success, Try}
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.*

object FileHandlingHelperMethods:
  def readTextFile(source: String):Option[Vector[String]] = {
    try {
      val stream: InputStream = getClass.getResourceAsStream(source)
      val lines: Vector[String] = scala.io.Source.fromInputStream(stream).getLines.toVector
      stream.close()
      Some(lines)
    } catch {
      case _: Throwable => None
    }
  }


  def readImage(source: String):Option[BufferedImage] = {
    val path = "/Resources/" + source

    Try(ImageIO.read(getClass().getResourceAsStream(path))) match
      case Failure(e) =>
        None
      case Success(bufferedImage) =>
        Some(bufferedImage)
  }
end FileHandlingHelperMethods
