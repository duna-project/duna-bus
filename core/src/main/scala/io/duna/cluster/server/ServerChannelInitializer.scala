package io.duna.cluster.server

import io.duna.cluster.protocol.{EventBusMessageDecoder, EventBusMessageEncoder}
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.{LengthFieldBasedFrameDecoder, LengthFieldPrepender}

private[server]
class ServerChannelInitializer extends ChannelInitializer[SocketChannel] {
  override def initChannel(ch: SocketChannel): Unit = {
    val pipeline = ch.pipeline()

    pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Int.MaxValue, 0, 4, 0, 0))
    pipeline.addLast("messageDecoder", new EventBusMessageDecoder)

    pipeline.addLast("frameEncoder", new LengthFieldPrepender(4, false))
    pipeline.addLast("messageEncoder", new EventBusMessageEncoder)
  }
}
