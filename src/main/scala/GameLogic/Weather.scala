package GameLogic

class Weather(val description: String,
              startTime      : Int,
              endTime        : Int,
              affectedRunways: Vector[Runway]
             ):

  def reactIfStartTime(currentTime: Int) =
    if currentTime == startTime then
      affectedRunways.foreach(_.startWeather(this))
  end reactIfStartTime
  
  def reactIfEndTime(currentTime: Int) =
    if currentTime == endTime then
      affectedRunways.foreach(_.endWeather())
  end reactIfEndTime
  
end Weather
