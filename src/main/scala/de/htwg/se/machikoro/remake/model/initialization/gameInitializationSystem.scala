package de.htwg.se.machikoro.remake.model.initialization

import de.htwg.se.machikoro.remake.model.Data.Gamestate

trait gameInitializationSystem {
  def apply(n: Int, gametype: String): Gamestate
}
