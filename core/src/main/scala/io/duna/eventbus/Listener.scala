package io.duna.eventbus

import io.duna.eventbus.message.Message

trait Listener[T] {

  val event: String

  def onReceive(handler: () => Unit): Listener[T] = {
    onReceive { _ => handler.apply() }
    this
  }

  def onReceive(handler: Option[T] => Unit): Listener[T]

  def onError(handler: Throwable => Unit): Listener[T]

  def when(matcher: (Option[_], Context) => Boolean): Listener[T]

  def stop(): Unit

  private[eventbus] def next(message: Message[_]): Unit

  private[eventbus] def matches(message: Message[_]): Boolean

}
