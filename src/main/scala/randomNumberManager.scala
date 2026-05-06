package de.htwg.se.machikoro.remake
import scala.collection.immutable.Queue
import scala.util.Random

object randomNumberManager {
  var NumQueue = Queue[Int]()
  val random = new Random()
  
  
  def getNextRandomNumber(): Int = {
    if (NumQueue.isEmpty) {
      return (random.nextInt(6) + 1)
    } else {
      val (element, newQueue) = NumQueue.dequeue
      NumQueue = newQueue
      val num = math.max(1, math.min(6, element))
      return num
    }
  }

  def writeIntoSimulatedRandomness(num: Int): Unit = {
    NumQueue = NumQueue.enqueue(num)
  }

}
