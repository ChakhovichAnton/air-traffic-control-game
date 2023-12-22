package FileHandling

import scala.util.{Failure, Success, Try}
import java.io.{BufferedWriter, File, FileWriter}
import org.joda.time.*

object GameReport:
  def writeGameReportFile(content: Vector[String]) =
    val name = getFileNameForFileReport
    val fN = "gameReports/" + dealWithTheSameName(name) + ".txt"
    val bw = new BufferedWriter(
      new FileWriter(
        new File(fN)
      )
    )
    (Vector(name) ++ content).foreach(l => bw.write(l + "\n"))
    bw.close()
  end writeGameReportFile

  private def dealWithTheSameName(newName: String):String =
    var currentName  = newName
    var nameNotFound = true
    var index        = 1

    while nameNotFound do
      if !FileHandlingHelperMethods
        .getFileList("gameReports", ".txt")
        .contains(currentName)
      then
        nameNotFound = false
      else if index == 1 then
        currentName += "_1"
      else
        currentName += index
      index += 1
    currentName
  end dealWithTheSameName

  private def getFileNameForFileReport: String =
    val fileNamePrefix = "ATC_Game_"

    def getFileNameTimeAPI:Option[String] =
      val timeMarker = "_"
      Try {
        val t = DateTime.now()
        t.getYear           + timeMarker +
          t.getMonthOfYear  + timeMarker +
          t.getDayOfMonth   + timeMarker +
          t.getHourOfDay    + timeMarker +
          t.getMinuteOfHour + timeMarker +
          t.getSecondOfMinute
      } match
        case Success(n) => Some(fileNamePrefix + n)
        case Failure(e) => None
    end getFileNameTimeAPI


    def getFileNameWithNumber(prefix: String) =
      val fileList = FileHandlingHelperMethods.getFileList("gameReports", ".txt")
          .filter(_.startsWith(fileNamePrefix))
          .drop(fileNamePrefix.length)
          .filter(_.toIntOption.isDefined)
          .map(_.toInt)

        var newName = ""
        var index = 0
        while newName.isEmpty do
          if !fileList.contains(index) then
            newName = index.toString
          index += 1
      fileNamePrefix + newName
    end getFileNameWithNumber


    getFileNameTimeAPI match
      case Some(name) => name
      case None =>
        val fileList = FileHandlingHelperMethods.getFileList("gameReports", ".txt")
          .filter(_.startsWith(fileNamePrefix))
          .drop(fileNamePrefix.length)
          .filter(_.toIntOption.isDefined)
          .map(_.toInt)

        var newName = ""
        var index = 0
        while newName.isEmpty do
          if !fileList.contains(index) then
            newName = index.toString
          index += 1
        fileNamePrefix + newName
  end getFileNameForFileReport

end GameReport
