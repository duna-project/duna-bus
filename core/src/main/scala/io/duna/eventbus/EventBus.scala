package io.duna.eventbus

import io.duna.eventbus.messaging.Message

trait EventBus {

  def >>[T](event: String): Emitter[T] = emit[T](event)

  def emit[T](event: String): Emitter[T]

  def <<[T](event: String): Subscriber[T] = subscribeTo[T](event)

  def subscribeTo[T](event: String): Subscriber[T]

  def -=(subscriber: Subscriber[_]): Unit = unsubscribe(subscriber)

  def unsubscribe(subscriber: Subscriber[_]): Unit

  def --=(event: String): Unit = unsubscribeAll(event)

  def unsubscribeAll(event: String): Unit

  def consume(message: Message[_])
}
