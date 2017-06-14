package io.duna.eventbus.message

import scala.reflect.ClassTag

protected[duna]
final class Signal(target: String,
                   headers: Map[Symbol, String] = Map.empty,
                   transmissionMode: TransmissionMode,
                   val signalType: SignalType)
  extends Message[Nothing](target, None, headers, None, transmissionMode)

object Signal {
  def unapply(message: Message[_]): Boolean =
    message match {
      case _: Signal => true
      case _ => false
    }
}

sealed class SignalType

object Completion extends SignalType {

  def apply(target: String,
            headers: Map[Symbol, String] = Map.empty,
            transmissionMode: TransmissionMode): Signal =
    new Signal(target, headers, transmissionMode, Completion)

  def unapply(message: Message[_]): Boolean =
    message match {
      case m: Signal if m.signalType == Completion => true
      case _ => false
    }
}