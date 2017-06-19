package io.duna.eventbus.message

import scala.reflect.runtime.universe.TypeTag

protected[duna]
final class Error[E <: Throwable : TypeTag](target: String,
                                             responseEvent: Option[String] = None,
                                             headers: Map[Symbol, String] = Map.empty,
                                             attachment: E,
                                             transmissionMode: TransmissionMode)
  extends Message[E](target, responseEvent, headers, Some(attachment), transmissionMode)

object Error {
  def unapply(message: Message[_ <: Throwable]): Option[_ <: Throwable] =
    message match {
      case m: Error[_] => m.attachment
      case _ => None
    }
}