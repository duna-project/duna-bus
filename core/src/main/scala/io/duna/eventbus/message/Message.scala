package io.duna.eventbus.message

import scala.reflect.ClassTag

protected[eventbus] case class Message[T: ClassTag](source: Option[String] = None,
                                                    target: String,
                                                    responseEvent: Option[String] = None,
                                                    headers: Map[String, String],
                                                    attachment: Option[T]) {

  lazy val attachmentType: Class[T] = implicitly[reflect.ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]

  def copyAsErrorMessage[E <: Throwable : ClassTag](exception: E): Message[E] = {
    Message(
      source = this.source,
      target = this.target,
      responseEvent = this.responseEvent,
      headers = this.headers,
      attachment = Some(exception)
    )
  }
}

object Header {

  def unapply(message: Message[_], key: String): Boolean =
    message.headers contains key

  def unapply(message: Message[_], key: String, value: String): Boolean =
    (message.headers contains key) && message.headers(key) == value
}