package ua.net.ipk0.twoSum.service

import ua.net.ipk0.twoSum.service.TwoSumProblemService.{Request, Response}

import scala.collection.immutable.HashSet

class TwoSumProblemService {

  def solve(target: Int, req: Request): Response =
    req.map(calculateItem(target, _))

  def calculateItem(targetNumber: Int, sourceDataSet: List[Int]): List[List[Int]] =
    sourceDataSet.foldLeft(State(List.empty[List[Int]], HashSet[Int](), targetNumber)){(state, item) =>
      val interest = state.targetNumber - item

      val (updatedHashSet, list) = if (state.hashSet.contains(interest)){
        state.hashSet - interest -> List(item, interest)
      } else {
        state.hashSet + item -> List.empty[Int]
      }

      state.copy(acc = list :: state.acc, hashSet = updatedHashSet)
    }.acc.filterNot(_.isEmpty)

/*
  def calculateItem(targetNumber: Int, sourceDataSet: List[Int]): List[List[Int]] = {
    val tempSet = mutable.HashSet[Int]()

    sourceDataSet.flatMap { item =>
      val interest = targetNumber - item

      if (tempSet.contains(interest)) {
        tempSet.remove(interest)
        Some(List(item, interest))
      } else {
        tempSet.add(item)
        None: Option[List[Int]]
      }
    }
  }
*/

  private case class State(acc: List[List[Int]], hashSet: HashSet[Int], targetNumber: Int)
}

object TwoSumProblemService {
  type Request = List[List[Int]]
  type Response = List[List[List[Int]]]

  def apply(): TwoSumProblemService = new TwoSumProblemService()
}
