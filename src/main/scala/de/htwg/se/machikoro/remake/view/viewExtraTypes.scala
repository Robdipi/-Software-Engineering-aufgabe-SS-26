package de.htwg.se.machikoro.remake.view

class viewCardData (val cardName : String = "Weizenfeld"
                    ,val price : Int = 0
                    ,val description : String = "Erhalte 1 Münze aus der Bank."
                    ,val texturePath : String = "")
class viewCardStack (val amount : Int,
                     val stackCard : viewCardData)
class viewPlayerData (playerProperties: List[viewCardData] = List(), money:Int)
class viewGamestate (Players : List[viewCardData], globalProperties : List[viewCardStack])