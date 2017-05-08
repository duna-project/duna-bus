package io.duna.eventbus

import scala.collection.mutable

class Context extends mutable.HashMap[String, String] {

  def currentEvent: String = ???

  def eventBus: EventBus = ???

}

object Context {

  private val contextHolder = new ThreadLocal[Context]

  def apply(): Context = contextHolder.get()

  def getCurrent: Context = apply()

  def setCurrent(value: Context): Unit = contextHolder.set(value)

}