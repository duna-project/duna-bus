package io.duna.eventbus

import scala.reflect.ClassTag

import io.duna.eventbus.message.Message

trait EventBus {

  def <~[T: ClassTag](event: String): Emitter[T] = emit(event)

  def ~>[T: ClassTag](event: String): Listener[T] = listenTo(event)

  def #>[T: ClassTag](event: String): Listener[T] = listenOnceTo(event)

  def -=(listener: Listener[_]): EventBus = {
    remove(listener)
    this
  }

  def --=(event: String): EventBus = {
    removeAll(event)
    this
  }

  def emit[T: ClassTag](event: String): Emitter[T]

  def listenTo[T: ClassTag](event: String): Listener[T]

  def listenOnceTo[T: ClassTag](event: String): Listener[T]

  def remove(subscriber: Listener[_]): Unit

  def removeAll(event: String): Unit

  def onError(handler: Throwable => Unit): Unit

  def consume(message: Message[_]): Unit
}
