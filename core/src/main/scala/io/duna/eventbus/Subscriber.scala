package io.duna.eventbus

trait Subscriber[T] {

  val event: String

  def onReceive(handler: Option[T] => Unit): Subscriber[T]

  def onError(handler: Option[Exception] => Unit): Subscriber[T]

  def unsubscribe(): Unit

  def next(arg: Option[T]): Unit

  def nextError(arg: _ <: Exception): Unit

}
