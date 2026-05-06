package de.htwg.se.machikoro.remake

import scala.io.StdIn.readLine

class InputManager (val InputString : String = "" ){
    def getNextInput(message : String): String = {
      if (InputString.equals("")) {
        return readLine(message)
      } else {
        return InputString;
      }
    }
}
