package de.htwg.se.machikoro.remake;

/*
  The Type of Industry the card is corresponding to the symbol in the grey circle. In the original game there is one Type more which is redundant so
  I merged it with the Industry Type. Types often get used for card effects
 */
enum Type {
  case Farm, Dairy, Industry, Restaurants,Store, Secondary_Industry, Major_Establishment, Landmark
}
/*
  Colors decide when the effect of a card is activated and other Rules
    Blue - No matter who roled that number the effect gets activated and the owner gets the associated money
    Green - If the owner roles that number
    Red - Anybody else than the owner roles the number the effect gets activated and that person has to pay
    Purple - If you role that Number + you can only own one purple Building
    Yellow - The goal of the Game is to buy all 4 Landmarks. Landmarks change game mechanics so their corresponding methods get called at the start of the turn to enable extra functionality. they offically dont have a role Number

 */
enum Color {
  case Blue, Green, Red, Purple, Yellow
}
/*
    cardName    name of the card
    price  -    price to buy one
    cardType    type see comment above
    roleNumbers  number/s that needs to be rolled for the effect to activate
    color       affects when the effect gets activated
    description - description for text output
    texturePath - where the texture is saved
    effect      - function which happens when the card needs to be executed
 */
case class card (val cardName : String = "Weizenfeld"
            ,val price : Int = 0
            ,val cardType : Type = Type.Farm
            ,val roleNumbers : Array[Int] = Array(1)
            ,val color : Color  = Color.Blue
            ,val description : String = "erhalte 1 Münze aus der Bank"
            ,val texturePath : String = ""
            ,val effect: (Gamestate, Int) => Gamestate
            ,val cardOwnerID: Int = -1) {
  /*
      When activate is called the corresponding function "effect" gets called.
      When a card is called depends on the Cardcolor
   */
  /*
  def activate(): Int = {
    println(s"$cardName is activated!")
    return 0
  }
  */

  def cardToString(): String = "|" + cardName + "|costs: " + price + "|" + description  + "|"
}