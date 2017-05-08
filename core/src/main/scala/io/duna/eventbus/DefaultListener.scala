package io.duna.eventbus

import scala.reflect.ClassTag

import io.duna.eventbus.message.Message

protected case class DefaultListener[T: ClassTag](override val event: String)
  extends Listener[T] {

  override def onReceive(handler: (Option[T]) => Unit): Listener[T] = ???

  override def onError(handler: (Throwable) => Unit): Listener[T] = ???

  override def when(matcher: (Option[_], Context) => Boolean): Listener[T] = ???

  override def stop(): Unit = ???

  override private[eventbus] def nextValue(value: Option[_]) = ???

  override private[eventbus] def nextError(error: Throwable) = ???

  override private[eventbus] def matches(message: Message[_]) = ???
}

object DefaultListener {
  private val MAX_PENDING_MESSAGES = 16
}
