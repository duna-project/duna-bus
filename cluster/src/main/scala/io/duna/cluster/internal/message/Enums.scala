package io.duna.cluster.internal.message

object MessageType extends Enumeration {
  type MessageType = Value
  val Event, Signal, Request, Error = Value
}

object SignalType extends Enumeration {
  type SignalType = Value
  val None, Completion = Value
}

object TransmissonMode extends Enumeration {
  type TransmissonMode = Value
  val Unicast, Multicast, Broadcast = Value
}