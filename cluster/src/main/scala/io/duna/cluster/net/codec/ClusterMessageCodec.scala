package cluster.net.codec

import java.util

import io.duna.cluster.internal.message.ClusterMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.{ByteToMessageCodec, ByteToMessageDecoder}

class ClusterMessageCodec
  extends ByteToMessageCodec[ClusterMessage] {

  override def encode(ctx: ChannelHandlerContext, msg: ClusterMessage, out: ByteBuf): Unit = {
    out.writeBytes(msg.toByteArray)
  }

  override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]): Unit = {
    val rcvdBytes = Array.ofDim[Byte](in.readableBytes())
    in.readBytes(rcvdBytes)
    in.release()

    val message = ClusterMessage.parseFrom(rcvdBytes)
    out.add(message)
  }
}
