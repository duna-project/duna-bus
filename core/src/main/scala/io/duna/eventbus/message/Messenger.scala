package io.duna.eventbus.message

protected[eventbus] trait Messenger {

  def send(message: Message[_]): Unit
}
