package de.htwg.se.machikoro.remake.controller.main

import de.htwg.se.machikoro.remake.model.Data.Gamestate

trait ViewObserver {
    def update(state : Gamestate): Unit
}
class ViewObservable  { //if I get an error change back to state
  private var observers:Vector[ViewObserver] = Vector()
  def add(s:ViewObserver): Unit = observers = observers:+s
  def remove(s:ViewObserver): Unit = observers = observers.filterNot(_ == s)
  def notifyObservers(state : Gamestate): Unit = observers.foreach(o=>o.update(state))
}