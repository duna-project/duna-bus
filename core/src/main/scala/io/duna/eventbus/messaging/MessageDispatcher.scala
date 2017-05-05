package io.duna.eventbus.messaging

protected[eventbus] trait MessageDispatcher {

  def dispatch(message: Message[_]): Unit
}
