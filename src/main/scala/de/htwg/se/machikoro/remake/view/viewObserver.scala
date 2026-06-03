package de.htwg.se.machikoro.remake.view

trait viewObserver {
    def update(state : viewGamestate): Unit
}
class viewObserverable (state : viewGamestate){
  var observers:Vector[viewObserver] = Vector()
  def add(s:viewObserver): Unit = observers = observers:+s
  def remove(s:viewObserver): Unit = observers = observers:+s
  def notifiyObservers: Unit = observers.foreach(o=>o.update(state))
}