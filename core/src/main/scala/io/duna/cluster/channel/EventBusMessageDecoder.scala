package io.duna.cluster.channel

import java.util

import io.duna.cluster.internal.message.ClusterMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class EventBusMessageDecoder
  extends ByteToMessageDecoder {

  override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]): Unit = {
    val rcvdBytes = Array.ofDim[Byte](in.readableBytes())
    in.readBytes(rcvdBytes)

    val message = ClusterMessage.parseFrom(rcvdBytes)
    out.add(message)
  }
}
