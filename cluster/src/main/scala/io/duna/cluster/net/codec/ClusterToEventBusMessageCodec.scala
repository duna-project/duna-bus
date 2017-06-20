package io.duna.cluster.net.codec

import java.util

import scala.reflect.runtime.universe.{TypeTag, typeOf, typeTag}

import com.google.protobuf.CodedInputStream
import io.duna.cluster.databind.Mapper
import io.duna.cluster.internal.message.{ClusterMessage, MessageType, SignalType, TransmissionMode}
import io.duna.cluster.reflect.TypeTagCache
import io.duna.eventbus.message._
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec

class ClusterToEventBusMessageCodec extends MessageToMessageCodec[ClusterMessage, Message[_]] {

  override def encode(ctx: ChannelHandlerContext, msg: Message[_], out: util.List[AnyRef]): Unit = {

    val messageType = msg match {
      case _: Event[_] => MessageType.EVENT
      case _: Request[_] => MessageType.REQUEST
      case _: Signal => MessageType.SIGNAL
      case _: Error[_] => MessageType.ERROR
    }

    val signalType = msg match {
      case signal: Signal =>
        signal.signalType match {
          case _: Completion.type => SignalType.COMPLETION
          case _ => SignalType.NONE
        }
      case _ =>
        SignalType.NONE
    }

    val transmissionMode = msg.transmissionMode match {
      case _: Unicast.type => TransmissionMode.UNICAST
      case _: Broadcast.type => TransmissionMode.BROADCAST
    }

    val attachment = msg.typeTag.tpe match {
      case tpe if tpe =:= typeOf[Nothing] || tpe =:= typeOf[Unit] || tpe =:= typeOf[Null] =>
        None
      case _ => msg.attachment match {
        case Some(obj) =>
          val attachmentBytes = Mapper.serialize(obj)
          Some(CodedInputStream.newInstance(attachmentBytes).readBytes())
        case None => None
      }
    }

    val attachmentType = msg.typeTag.tpe match {
      case tpe if tpe =:= typeOf[Nothing] || tpe =:= typeOf[Unit] || tpe =:= typeOf[Null] => None
      case _ => Some(msg.typeTag.toString())
    }

    val clusterMessage = ClusterMessage(
      messageType,
      msg.source, msg.target, msg.responseEvent,
      attachment, attachmentType,
      msg.headers.map { case (a: Symbol, b: String) => (a.toString(), b) },
      transmissionMode, signalType)

    out.add(clusterMessage)
  }

  override def decode(ctx: ChannelHandlerContext, msg: ClusterMessage, out: util.List[AnyRef]): Unit = {
    val attTypeTag = msg.attachmentType match {
      case None => typeTag[Unit]
      case Some(t) => TypeTagCache.get(t)
    }

    msg.messageType match {
      case _: MessageType.EVENT.type =>
        Event(msg.source, msg.target, )
      case _: MessageType.REQUEST.type =>
      case _: MessageType.SIGNAL.type =>
      case _: MessageType.ERROR.type =>
    }
  }

}

object ClusterToEventBusMessageCodec {

  implicit class TypeTagExtension(val self: TypeTag[_]) extends AnyVal {

    def typeName: String = ???

  }

}
