package io.duna.eventbus

import io.duna.eventbus.messaging.Message

trait EventBus {

  def emit[T](event: String): Emitter[T]

  def subscribeTo[T](event: String): Subscriber[T]

  def unsubscribe(subscriber: Subscriber[_])

  def unsubscribeAll(event: String)

  def consume(message: Message[_])
}

object EventBus {
  def apply(): EventBus = ???
}
