import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import scala.collection.mutable.Buffer
import GameLogic.*

/**
 * Class used for testing plane crashes when planes have the same coordinates.
 */
class CrashTest extends AnyFlatSpec:
  /**
   * Read more on this in the Final Document. I've used the following tests to test
   * that the function works correctly.
   * I assume that planes can only have positive (0 to n) x and y coordinates.
   * @param planes all of the planes inside a vector.
   *               Planes have a width of 20 and they are represented by rectangles.
   * @return the distinct planes that have crashed.
   */
  def crash(planes: Vector[Airplane]): Vector[Airplane] =
    val crashes = Buffer[Airplane]()
    for
      p1 <- planes
      p2 <- planes
    do
      if p1 != p2 &&
        p1.getCoordinates.isDefined &&
        p2.getCoordinates.isDefined && (
        p1.getCoordinates.get._1      <= p2.getCoordinates.get._1 &&
        p1.getCoordinates.get._1 + 20 >= p2.getCoordinates.get._1 &&
        p1.getCoordinates.get._2 + 20 >= p2.getCoordinates.get._2 &&
        p1.getCoordinates.get._2      <= p2.getCoordinates.get._2
        ||
        p1.getCoordinates.get._1      <= p2.getCoordinates.get._1 &&
        p1.getCoordinates.get._1 + 20 >= p2.getCoordinates.get._1 &&
        p1.getCoordinates.get._2 + 20 >= p2.getCoordinates.get._2 + 20 &&
        p1.getCoordinates.get._2      <= p2.getCoordinates.get._2 + 20
        ||
        p1.getCoordinates.get._1      <= p2.getCoordinates.get._1 + 20 &&
        p1.getCoordinates.get._1 + 20 >= p2.getCoordinates.get._1 + 20 &&
        p1.getCoordinates.get._2 + 20 >= p2.getCoordinates.get._2 &&
        p1.getCoordinates.get._2      <= p2.getCoordinates.get._2
        ||
        p1.getCoordinates.get._1      <= p2.getCoordinates.get._1 + 20 &&
        p1.getCoordinates.get._1 + 20 >= p2.getCoordinates.get._1 + 20 &&
        p1.getCoordinates.get._2 + 20 >= p2.getCoordinates.get._2 + 20 &&
        p1.getCoordinates.get._2      <= p2.getCoordinates.get._2 + 20)
      then
        crashes += p1
        crashes += p2
    crashes.distinct.toVector
  end crash

  "Crash1" should "return both planes, while other planes are right next to the two colliding planes" in {
    val plane1 = Airplane(1, 1, 1, 1, 1, 1, "")
    val plane2 = Airplane(2, 1, 1, 1, 1, 1, "")
    val plane3 = Airplane(3, 1, 1, 1, 1, 1, "")
    val plane4 = Airplane(4, 1, 1, 1, 1, 1, "")

    plane1.setCoordinates(0,0)
    plane2.setCoordinates(15, 15)
    plane3.setCoordinates(0, 36)
    plane4.setCoordinates(36, 0)

    val planes = Vector(plane1, plane2, plane3, plane4)

    val crashes: Vector[Airplane] = crash(planes)

    assert(crashes.contains(plane1))
    assert(crashes.contains(plane2))
    assert(crashes.size === 2)
  }

  "Crash2" should "return three planes, while three planes collide with each other" in {
    val plane1 = Airplane(1, 1, 1, 1, 1, 1, "")
    val plane2 = Airplane(2, 1, 1, 1, 1, 1, "")
    val plane3 = Airplane(3, 1, 1, 1, 1, 1, "")
    val plane4 = Airplane(4, 1, 1, 1, 1, 1, "")

    plane1.setCoordinates(0,0)
    plane2.setCoordinates(15, 15)
    plane3.setCoordinates(0, 35)
    plane4.setCoordinates(36, 0)

    val planes = Vector(plane1, plane2, plane3, plane4)

    val crashes: Vector[Airplane] = crash(planes)

    assert(crashes.contains(plane1))
    assert(crashes.contains(plane2))
    assert(crashes.contains(plane3))
    assert(crashes.size === 3)
  }

  "Crash3" should "return two planes, when one is bottom left from the other" in {
    val plane1 = Airplane(1, 1, 1, 1, 1, 1, "")
    val plane2 = Airplane(2, 1, 1, 1, 1, 1, "")
    val plane3 = Airplane(3, 1, 1, 1, 1, 1, "")
    val plane4 = Airplane(4, 1, 1, 1, 1, 1, "")

    plane1.setCoordinates(10,10)
    plane2.setCoordinates(31, 15)
    plane3.setCoordinates(0, 50)
    plane4.setCoordinates(3, 20)

    val planes = Vector(plane1, plane2, plane3, plane4)

    val crashes: Vector[Airplane] = crash(planes)

    assert(crashes.contains(plane1))
    assert(crashes.contains(plane4))
    assert(crashes.size === 2)
  }

  "Crash4" should "return two planes, while one of them is inside one of them." in {
    val plane1 = Airplane(1, 1, 1, 1, 1, 1, "")
    val plane2 = Airplane(2, 1, 1, 1, 1, 1, "")
    val plane3 = Airplane(3, 1, 1, 1, 1, 1, "")
    val plane4 = Airplane(4, 1, 1, 1, 1, 1, "")

    plane1.setCoordinates(10,10)
    plane2.setCoordinates(31, 15)
    plane3.setCoordinates(0, 50)
    plane4.setCoordinates(13, 15)

    val planes = Vector(plane1, plane2, plane3, plane4)

    val crashes: Vector[Airplane] = crash(planes)

    assert(crashes.contains(plane1))
    assert(crashes.contains(plane4))
    assert(crashes.size === 2)
  }

  "Crash5" should "return two planes, when one is top right from the other" in {
    val plane1 = Airplane(1, 1, 1, 1, 1, 1, "")
    val plane2 = Airplane(2, 1, 1, 1, 1, 1, "")
    val plane3 = Airplane(3, 1, 1, 1, 1, 1, "")
    val plane4 = Airplane(4, 1, 1, 1, 1, 1, "")

    plane1.setCoordinates(10,10)
    plane2.setCoordinates(31, 71)
    plane3.setCoordinates(0, 50)
    plane4.setCoordinates(30, 5)

    val planes = Vector(plane1, plane2, plane3, plane4)

    val crashes: Vector[Airplane] = crash(planes)

    assert(crashes.contains(plane1))
    assert(crashes.contains(plane4))
    assert(crashes.size === 2)
  }

end CrashTest
