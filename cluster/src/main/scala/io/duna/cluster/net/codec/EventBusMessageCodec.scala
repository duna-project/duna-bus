package io.duna.cluster.net.codec

import java.util

import com.google.protobuf.CodedInputStream
import io.duna.cluster.internal.message.{ClusterMessage, SignalType, TransmissionMode}
import io.duna.eventbus.message._
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec

class EventBusMessageCodec extends MessageToMessageCodec[ClusterMessage, Message[_]] {

  override def encode(ctx: ChannelHandlerContext, msg: Message[_], out: util.List[AnyRef]): Unit = {
    import EventBusMessageCodec._

    val signalType = msg match {
      case s: Signal if s.signalType.isInstanceOf[Completion.type] =>
        SignalType.COMPLETION
      case _ =>
        SignalType.NONE
    }

    val transmissionMode = msg.transmissionMode match {
      case _: Unicast.type =>
        TransmissionMode.UNICAST
      case _ =>
        TransmissionMode.BROADCAST
    }

    val attachment = msg match {
      case _: Message[Nothing] |
           _: Message[Unit] |
           _: Message[Null] =>
        None
      case _ =>
        val attachmentBytes = CodedInputStream.newInstance(Array.ofDim[Byte](1))
        None
    }

//    val clusterMessage = ClusterMessage(msg.source, msg.target, msg.responseEvent,
//      attachment,
//      msg.headers.map { case (a: Symbol, b: String) => (a.toString(), b) },
//      transmissionMode,
//      signalType)
//
//    out.add(clusterMessage)
  }

  override def decode(ctx: ChannelHandlerContext, msg: ClusterMessage, out: util.List[AnyRef]): Unit = ???
}

object EventBusMessageCodec {

  implicit class ClassNameExtractor(val self: Class[_]) extends AnyVal {
    def typeName: String = self match {
      case c if c == classOf[Byte] => "byte"

      case c if c == classOf[Short] => "int16"
      case c if c == classOf[Int] => "int32"
      case c if c == classOf[Long] => "int64"

      case c if c == classOf[Float] => "float"
      case c if c == classOf[Double] => "double"

      case c if c == classOf[Char] => "char"
      case c if c == classOf[Boolean] => "boolean"

      case c if c == classOf[Unit] => "unit"
      case c if c == classOf[Nothing] => "nothing"

      case c if c.getName == "" => ""

      case _ => self.getTypeName
    }

    def fromTypeName(typeName: String): Class[_] = typeName match {
      case "byte" => classOf[Byte]

      case "int16" => classOf[Short]
      case "int32" => classOf[Int]
      case "int64" => classOf[Long]

      case "float" => classOf[Float]
      case "double" => classOf[Double]

      case "char" => classOf[Char]
      case "boolean" => classOf[Boolean]

      case className => null
    }
  }

}
