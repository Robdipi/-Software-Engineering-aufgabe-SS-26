import de.htwg.se.machikoro.remake.randomNumberManager.random

import scala.util.Random

class RandomnessManager (val Dicethrows : Int = -1){
    def getNextNum(): Int ={
      val random = new Random()
      if(Dicethrows == -1){
        return (random.nextInt(6) + 1)
      } else {
        return Dicethrows;
      }
    }
}
