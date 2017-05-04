package io.duna.eventbus.messaging

import scala.reflect.ClassTag

protected[eventbus] case class Message[+T: ClassTag](source: Option[String] = None,
                                                     target: String,
                                                     responseEvent: Option[String] = None,
                                                     headers: Map[String, String],
                                                     attachment: Option[T])

object Header {

  def unapply(message: Message[_], key: String): Boolean =
    message.headers contains key

  def unapply(message: Message[_], key: String, value: String): Boolean =
    (message.headers contains key) && message.headers(key) == value
}