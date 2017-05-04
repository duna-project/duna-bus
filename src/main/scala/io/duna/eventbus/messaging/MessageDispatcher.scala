package io.duna.eventbus.messaging

protected[eventbus] trait MessageDispatcher[T] {

  def dispatch(message: Message[T]): Unit = ???
}
