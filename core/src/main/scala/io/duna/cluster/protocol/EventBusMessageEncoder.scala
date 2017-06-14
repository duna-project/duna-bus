package io.duna.cluster.protocol

import io.duna.cluster.internal.message.ClusterMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class EventBusMessageEncoder extends MessageToByteEncoder[ClusterMessage] {
  override def encode(ctx: ChannelHandlerContext, msg: ClusterMessage, out: ByteBuf): Unit = {
    out.writeBytes(msg.toByteArray)
  }
}
