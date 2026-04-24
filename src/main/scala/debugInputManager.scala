package de.htwg.se.machikoro.remake
import scala.collection.immutable.Queue
import scala.io.StdIn.readLine

object debugInputManager {
  var InputQueue = Queue[String]()


  def readForTestAndGamePurposes(message:String): String = {
    if(InputQueue.isEmpty){
      return readLine(message)
    }else{
      val (element, newQueue) = InputQueue.dequeue
      InputQueue = newQueue
      return element
    }
  }

  def writeIntoSimulatedChat(message: String): Unit = {
    InputQueue = InputQueue.enqueue("hello")
  }

}
