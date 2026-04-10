1+1==4
var socke = 123
socke = socke * 2
val hallo = "Guten Abend"

enum Type {
  case Farm, Dairy, Industry, Restaurants,Store, Secondary_Industry, Major_Establishment, Landmark
}

enum Color {
  case Blue, Green, Red, Purple, Yellow
}

def methode(): Unit = println(hallo)

class card (val cardName : String = "Weizenfeld"
            ,val price : Int = 0
            ,val cardType : Type = Type.Farm
            ,val roleNumbers : Array[Int] = Array(1)
            ,val color : Color  = Color.Blue
            ,val description : String = "erhalte 1 Münze aus der Bank"
            ,val texturePath : String = " "
            ,val effect: () => Unit = ) {

  def activate(): Unit = {
    println(s"$cardName is activated!")
    effect()
  }

  def cardToString(): String = cardName + "\n" + price + "\n" + description
}

val c1  = new card()