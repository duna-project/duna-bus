package io.duna.eventbus

import io.duna.eventbus.message.Message

trait Listener[T] {

  val event: String

  def onReceive(handler: Option[T] => Unit): Listener[T]

  def onError(handler: Throwable => Unit): Listener[T]

  def when(matcher: (Option[_], Context) => Boolean): Listener[T]

  def stop(): Unit

  private[eventbus] def nextValue(value: Option[_]): Unit

  private[eventbus] def nextError(error: Throwable): Unit

  private[eventbus] def matches(message: Message[_]): Boolean

}
