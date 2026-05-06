package de.htwg.se.machikoro.remake

import scala.io.StdIn.readLine

case class InputManager(inputs: List[String] = Nil, index: Int = 0) {
  def getNextInput(message: String): (String, InputManager) = {
    if (inputs.isEmpty) {
      val value = scala.io.StdIn.readLine(message)
      (value, this)
    } else {
      val value = inputs(index)
      val nextIndex = (index + 1) % inputs.length
      (value, copy(index = nextIndex))
    }
  }
}
