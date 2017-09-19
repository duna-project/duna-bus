package io.duna.cluster.util

import io.duna.cluster.internal.{message => cluster}
import io.duna.eventbus.{message => eventbus}

object MessageConversions {

  implicit class ClusterSignalTypeConversion(val self: cluster.SignalType) extends AnyVal {
    def toEventBus: eventbus.SignalType = self match {
      case cluster.SignalType.NONE => eventbus.Unrecognized
      case cluster.SignalType.COMPLETION => eventbus.Completion
      case cluster.SignalType.Unrecognized(_) => eventbus.Unrecognized
    }
  }

  implicit class ClusterTransmissionModeConversion(val self: cluster.TransmissionMode) extends AnyVal {
    def toEventBus: eventbus.TransmissionMode = self match {
      case cluster.TransmissionMode.UNICAST => eventbus.Unicast
      case cluster.TransmissionMode.MULTICAST => eventbus.Unicast
      case cluster.TransmissionMode.BROADCAST => eventbus.Broadcast
      case cluster.TransmissionMode.Unrecognized(_) => eventbus.Unicast
    }
  }

  implicit class ClusterHeadersConversion(val self: Map[String, String]) extends AnyVal {
    def toEventBus: Map[Symbol, String] = self.map { case (a, b) => (Symbol(a), b) }
  }

  implicit class EventBusMessageConversions(val self: eventbus.Message[_]) extends AnyVal {

    def clusterTransmissionMode: cluster.TransmissionMode = self.transmissionMode match {
      case eventbus.Unicast => cluster.TransmissionMode.UNICAST
      case eventbus.Broadcast => cluster.TransmissionMode.BROADCAST
    }

    def clusterSignalType: cluster.SignalType = self match {
      case s: eventbus.Signal =>
        s.signalType match {
          case eventbus.Completion => cluster.SignalType.COMPLETION
          case _ => cluster.SignalType.NONE
        }
      case _ => cluster.SignalType.NONE
    }

    def clusterHeaders: Map[String, String] = self.headers.map { case (a, b) => (a.name, b) }

    def clusterMessageType: cluster.Enums = self match {
      case _: eventbus.Event[_] => cluster.Enums.EVENT
      case _: eventbus.Request[_] => cluster.Enums.REQUEST
      case _: eventbus.Error[_] => cluster.Enums.ERROR
      case _: eventbus.Signal => cluster.Enums.SIGNAL
      case _ => cluster.Enums.EVENT
    }
  }

  implicit class EventBusHeadersConversion(val self: Map[Symbol, String]) extends AnyVal {

  }

}
