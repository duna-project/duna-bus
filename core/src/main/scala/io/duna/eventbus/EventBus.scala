package io.duna.eventbus

import scala.reflect.ClassTag

import io.duna.eventbus.message.Message

trait EventBus {

  def <~[T: ClassTag](event: String): Emitter[T] = this.emit[T](event)

  def emit[T: ClassTag](event: String): Emitter[T]

  def ~>[T: ClassTag](event: String): Listener[T] = listenTo[T](event)

  def listenTo[T: ClassTag](event: String): Listener[T]

  def -=(subscriber: Listener[_]): Unit = unsubscribe(subscriber)

  def unsubscribe(subscriber: Listener[_]): Unit

  def --=(event: String): Unit = unsubscribeAll(event)

  def unsubscribeAll(event: String): Unit

  def consume(message: Message[_])
}
