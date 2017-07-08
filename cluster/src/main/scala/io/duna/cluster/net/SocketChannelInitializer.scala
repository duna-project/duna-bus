package io.duna.cluster.net

import io.duna.cluster.net.codec.{ClusterMessageCodec, ClusterToEventBusMessageCodec}
import io.netty.channel.socket.SocketChannel
import io.netty.channel.{ChannelInboundHandler, ChannelInitializer}
import io.netty.handler.codec.protobuf.{ProtobufVarint32FrameDecoder, ProtobufVarint32LengthFieldPrepender}
import io.netty.handler.ssl.SslContext

class SocketChannelInitializer(sslContext: Option[SslContext],
                               channelHandler: Option[ChannelInboundHandler] = None)
  extends ChannelInitializer[SocketChannel] {

  override def initChannel(ch: SocketChannel): Unit = {
    val pipeline = ch.pipeline()

    if (sslContext.isDefined) {
      pipeline.addLast("ssl", sslContext.get.newHandler(ch.alloc()))
    }

    pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder)
    pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender)

    pipeline.addLast("clusterMessageCodec", new ClusterMessageCodec)
    pipeline.addLast("eventBusMessageCodec", new ClusterToEventBusMessageCodec)

    if (channelHandler.isDefined) {
      pipeline.addLast("handler", channelHandler.get)
    }
  }
}
