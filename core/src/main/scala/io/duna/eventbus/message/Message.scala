package io.duna.eventbus.message

import scala.reflect.runtime.universe.TypeTag
import scala.util.Try

import io.duna.eventbus.Context

protected[duna]
sealed abstract case class Message[A: TypeTag](source: Option[String] = Try(Context.current.currentEvent).toOption,
                                               target: String,
                                               responseEvent: Option[String] = None,
                                               headers: Map[Symbol, String] = Map.empty,
                                               attachment: Option[A] = None,
                                               transmissionMode: TransmissionMode) {

  lazy val typeTag: TypeTag[A] = implicitly[TypeTag[A]]

  lazy val attachmentType: Class[A] = typeTag.mirror.runtimeClass(typeTag.tpe).asInstanceOf[Class[A]]

}

protected[duna]
class Event[A: TypeTag](source: Option[String] = Try(Context.current.currentEvent).toOption,
                        target: String,
                        headers: Map[Symbol, String] = Map.empty,
                        attachment: Option[A] = None,
                        transmissionMode: TransmissionMode)
  extends Message[A](source, target, None, headers, attachment, transmissionMode) {

}

protected[duna]
final class Request[A: TypeTag](source: Option[String] = Try(Context.current.currentEvent).toOption,
                                target: String,
                                responseEvent: Option[String] = None,
                                headers: Map[Symbol, String] = Map.empty,
                                attachment: Option[A] = None)
  extends Message[A](source, target, responseEvent, headers, attachment, Unicast)

protected[duna]
final class Signal(source: Option[String] = Try(Context.current.currentEvent).toOption,
                   target: String,
                   headers: Map[Symbol, String] = Map.empty,
                   transmissionMode: TransmissionMode,
                   val signalType: SignalType)
  extends Message[Nothing](source, target, None, headers, None, transmissionMode)

protected[duna]
final class Error[E <: Throwable : TypeTag](source: Option[String] = Try(Context.current.currentEvent).toOption,
                                            target: String,
                                            responseEvent: Option[String] = None,
                                            headers: Map[Symbol, String] = Map.empty,
                                            attachment: E,
                                            transmissionMode: TransmissionMode)
  extends Message[E](source, target, responseEvent, headers, Some(attachment), transmissionMode)

protected[duna]
object Event {

  def apply[A: TypeTag](source: Option[String],
                        target: String,
                        headers: Map[Symbol, String],
                        attachment: Option[A],
                        transmissionMode: TransmissionMode): Event[A] =
    new Event(source, target, headers, attachment, transmissionMode)

  def apply[A: TypeTag](target: String,
                        headers: Map[Symbol, String] = Map.empty,
                        attachment: Option[A] = None,
                        transmissionMode: TransmissionMode): Event[A] =
    new Event(Try(Context.current.currentEvent).toOption, target, headers, attachment, transmissionMode)

  def unapply(message: Message[_]): Option[_] =
    message match {
      case _: Event[_] => message.attachment
      case _ => None
    }
}

object Request {

  def apply[A: TypeTag](source: Option[String],
                        target: String,
                        responseEvent: Option[String],
                        headers: Map[Symbol, String],
                        attachment: Option[A]): Request[A] =
    new Request(source, target, responseEvent, headers, attachment)

  def apply[A: TypeTag](target: String,
                        responseEvent: Option[String] = None,
                        headers: Map[Symbol, String] = Map.empty,
                        attachment: Option[A] = None): Request[A] =
    new Request(Try(Context.current.currentEvent).toOption, target, responseEvent, headers, attachment)

  def unapply(message: Message[_]): Option[_] =
    message match {
      case _: Request[_] => message.attachment
      case _ => None
    }
}

protected[duna]
object Signal {

  def apply(source: Option[String],
            target: String,
            headers: Map[Symbol, String],
            transmissionMode: TransmissionMode,
            signalType: SignalType): Signal =
    new Signal(source, target, headers, transmissionMode, signalType)

  def apply(target: String,
            headers: Map[Symbol, String] = Map.empty,
            transmissionMode: TransmissionMode,
            signalType: SignalType): Signal =
    new Signal(Try(Context.current.currentEvent).toOption, target, headers, transmissionMode, signalType)

  def unapply(message: Message[_]): Option[Signal] =
    message match {
      case _: Signal => Some(message.asInstanceOf[Signal])
      case _ => None
    }
}

object Error {

  def apply[E <: Throwable : TypeTag](source: Option[String],
                                      target: String,
                                      responseEvent: Option[String],
                                      headers: Map[Symbol, String],
                                      attachment: E,
                                      transmissionMode: TransmissionMode): Error[E] =
    new Error(source, target, responseEvent, headers, attachment, transmissionMode)

  def apply[E <: Throwable : TypeTag](target: String,
                                      responseEvent: Option[String] = None,
                                      headers: Map[Symbol, String] = Map.empty,
                                      attachment: E,
                                      transmissionMode: TransmissionMode): Error[E] =
    new Error(Try(Context.current.currentEvent).toOption,
      target, responseEvent, headers, attachment, transmissionMode)

  def unapply(message: Message[_ <: Throwable]): Option[_ <: Throwable] =
    message match {
      case m: Error[_] => m.attachment
      case _ => None
    }
}