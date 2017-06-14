package io.duna.eventbus.message

protected[duna] trait Postman {

  def deliver(message: Message[_]): Unit

}
