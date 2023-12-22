package GameLogic

trait Direction:
  def getPlaneRotation: Int
end Direction


case class North() extends Direction:
  override def getPlaneRotation: Int = 270


case class East() extends Direction:
  override def getPlaneRotation: Int = 0


case class South() extends Direction:
  override def getPlaneRotation: Int = 90


case class West() extends Direction:
  override def getPlaneRotation: Int = 180
