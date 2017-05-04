package io.duna.eventbus

trait Subscriber[T] {

  val event: String

  def onReceive(handler: (Option[T]) => Unit): Subscriber[T]

  def onError(handler: (Exception) => Unit): Subscriber[T]

}
