package de.htwg.se.machikoro.remake.model.initialization

import de.htwg.se.machikoro.remake.model.Data.Gamestate

trait GameInitializationSystem {
  def apply(n: Int, gametype: String): Gamestate
}
