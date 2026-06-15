package de.htwg.se.machikoro.remake.controller.main

import de.htwg.se.machikoro.remake.model.Data.Gamestate

trait viewObserver {
    def update(state : Gamestate): Unit
}
class ViewObservable  { //if I get an error change back to state
  var observers:Vector[viewObserver] = Vector()
  def add(s:viewObserver): Unit = observers = observers:+s
  def remove(s:viewObserver): Unit = observers = observers.filterNot(_ == s)
  def notifiyObservers(state : Gamestate): Unit = observers.foreach(o=>o.update(state))
}