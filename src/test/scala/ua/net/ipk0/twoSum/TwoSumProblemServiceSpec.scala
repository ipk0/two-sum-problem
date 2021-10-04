package ua.net.ipk0.twoSum

import org.scalatest.{FlatSpec, Matchers}
import ua.net.ipk0.twoSum.service.TwoSumProblemService

class TwoSumProblemServiceSpec extends FlatSpec with Matchers {

  it should "solve the problem within predefined datasets" in new TwoSumProblemService {
    solve(1, List(List(-1, 0, 2, 5), List(11, 5, 9, -4, -12))) shouldBe List(List(List(2, -1)), List(List(-4, 5)))
    solve(3, List(List(-1, 0, 2, 1), List(11, -8, 9, -4, -5))) shouldBe List(List(List(1, 2)), List(List(-8, 11)))
    solve(9, List(List(0, 1, 2, 5), List(8, 9, 1, -4, -5))) shouldBe List(List(), List(List(1, 8)))
    solve(-5, List(List(5, -10, 2, 4), List(1, 2, 9, -14, -5))) shouldBe List(List(List(-10, 5)), List(List(-14, 9)))
    solve(0, List(List(0, 2, -20, 20), List(15, 3, 8, -3, -5))) shouldBe List(List(List(20, -20)), List(List(-3, 3)))
  }
}
