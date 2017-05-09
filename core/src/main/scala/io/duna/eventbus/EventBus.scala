package io.duna.eventbus

import scala.reflect.ClassTag

import io.duna.eventbus.message.Message

trait EventBus {

  def <~[T: ClassTag](event: String): Emitter[T] = emit(event)

  def ~>[T: ClassTag](event: String): Listener[T] = listenTo(event)

  def #>[T: ClassTag](event: String): Listener[T] = listenOnceTo(event)

  def >~[T: ClassTag](attachment: Option[T]): Unit = respondWith(attachment)

  def -=(listener: Listener[_]): Unit = remove(listener)

  def --=(event: String): Unit = removeAll(event)

  def emit[T: ClassTag](event: String): Emitter[T]

  def listenTo[T: ClassTag](event: String): Listener[T]

  def listenOnceTo[T: ClassTag](event: String): Listener[T]

  def respondWith[T: ClassTag](attachment: Option[T]): Unit

  def remove(subscriber: Listener[_]): Unit

  def removeAll(event: String): Unit

  def consume(message: Message[_])
}
