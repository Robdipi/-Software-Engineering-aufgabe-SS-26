package de.htwg.se.machikoro.remake.controller

import de.htwg.se.machikoro.remake.model.*

trait viewObserver {
    def update(state : Gamestate): Unit
}
class viewObserverable {
  var observers:Vector[viewObserver] = Vector()
  def add(s:viewObserver): Unit = observers = observers:+s
  def remove(s:viewObserver): Unit = observers = observers.filterNot(_ == s)
  def notifiyObservers(state : Gamestate): Unit = observers.foreach(o=>o.update(state))
}