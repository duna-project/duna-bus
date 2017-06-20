package io.duna.eventbus.message

import scala.reflect.runtime.universe.TypeTag
import scala.util.Try

import io.duna.eventbus.Context

protected[duna]
class Event[A: TypeTag](source: Option[String] = Try(Context.current.currentEvent).toOption,
                        target: String,
                        headers: Map[Symbol, String] = Map.empty,
                        attachment: Option[A] = None,
                        transmissionMode: TransmissionMode)
  extends Message[A](source, target, None, headers, attachment, transmissionMode) {

}

object Event {
  def unapply(message: Message[_]): Option[_] =
    message match {
      case _: Event[_] => message.attachment
      case _ => None
    }
}