package io.duna.eventbus.message

sealed class SignalType

object Unrecognized extends SignalType

object Completion extends SignalType {
  def unapply(message: Message[_]): Boolean =
    message match {
      case m: Signal if m.signalType == Completion => true
      case _ => false
    }

  def unapply(signal: Signal): Boolean =
    signal match {
      case m if m.signalType == Completion => true
      case _ => false
    }
}