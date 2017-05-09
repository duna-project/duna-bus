package io.duna.eventbus

import scala.collection.mutable

import io.duna.eventbus.message.Message

class Context(val currentEvent: String,
              val respondTo: String,
              val owner: EventBus) extends mutable.HashMap[String, String] {

  def assign(): Unit = Context.setCurrent(this)
}

object Context {

  private val contextHolder = new ThreadLocal[Context]

  def apply(): Context = contextHolder.get()

  def getCurrent: Context = apply()

  def setCurrent(value: Context): Unit = contextHolder.set(value)

  def createFrom(message: Message[_], owner: EventBus = null): Context = {
    val context = new Context(message.target, message.responseEvent.orNull, owner)
    context ++= message.headers
  }
}