package de.htwg.se.machikoro.remake

import de.htwg.se.machikoro.remake

trait viewObserver {
    def update: Unit
}
class viewObserverable {
  var observers:Vector[viewObserver] = Vector()
  def add(s:viewObserver): Unit = observers = observers:+s
  def remove(s:viewObserver): Unit = observers = observers:+s
  def notifiyObservers: Unit = observers.foreach(o=>o.update)
}