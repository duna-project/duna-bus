package io.duna.cluster.codec

import java.util

import io.duna.cluster.internal.message.ClusterMessage
import io.duna.cluster.net.codec.ClusterToEventBusMessageCodec
import io.duna.eventbus.message.{Event, Message, Unicast}
import org.scalatest.FlatSpec

class ClusterToEventBusMessageCodecSpec extends FlatSpec {

  behavior of "The Codec"

  it must "convert an eventbus message to a cluster message and back" in {
    val attachment = SampleAttachment("Some Attachment")
    val message = Event(Some("source"), "target", Map('header -> "value"), Some(attachment), Unicast)

    val codec = new ClusterToEventBusMessageCodec

    val encoded = new util.LinkedList[AnyRef]()
    codec.encode(null, message, encoded)

    assertResult(1)(encoded.size())

    val decoded = new util.LinkedList[AnyRef]()
    codec.decode(null, encoded.get(0).asInstanceOf[ClusterMessage], decoded)

    assertResult(1)(decoded.size())
    assert(decoded.get(0).asInstanceOf[Message[_]].typeTag.tpe =:= message.typeTag.tpe)
    assert(decoded.get(0) == message)
  }
}

case class SampleAttachment(value: String)