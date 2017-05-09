package io.duna.eventbus.message

protected[eventbus] trait Postman {

  def deliver(message: Message[_]): Unit
}
