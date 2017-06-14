package io.duna.eventbus.message

import scala.reflect.ClassTag

protected[duna]
final class Request[A: ClassTag](target: String,
                                 responseEvent: Option[String] = None,
                                 headers: Map[Symbol, String] = Map.empty,
                                 attachment: Option[A] = None)
  extends Message[A](target, responseEvent, headers, attachment, Unicast)

object Request {
  def unapply(message: Message[_]): Option[_] =
    message match {
      case _: Request[_] => message.attachment
      case _ => None
    }
}