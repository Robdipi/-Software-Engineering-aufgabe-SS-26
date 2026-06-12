package de.htwg.se.machikoro.remake.controller.main.impl1

import scala.util.Random

case class RandomnessManager(numbers: List[Int] = Nil, index: Int = 0) {

  def getNextNum: (Int, RandomnessManager) = {
    if (numbers.isEmpty) {
      val value = Random.nextInt(6) + 1
      (value, this)
    } else {
      val value = numbers(index)
      val nextIndex = (index + 1) % numbers.length
      (value, this.copy(index = nextIndex))
    }
  }
}

