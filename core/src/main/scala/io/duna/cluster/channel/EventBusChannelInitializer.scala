package io.duna.cluster.channel

import io.netty.channel.socket.SocketChannel
import io.netty.channel.{ChannelInboundHandler, ChannelInitializer}
import io.netty.handler.codec.protobuf.{ProtobufVarint32FrameDecoder, ProtobufVarint32LengthFieldPrepender}
import io.netty.handler.ssl.SslContext

class EventBusChannelInitializer(sslContext: Option[SslContext],
                                 inboundHandler: Option[ChannelInboundHandler] = None)
  extends ChannelInitializer[SocketChannel] {

  override def initChannel(ch: SocketChannel): Unit = {
    val pipeline = ch.pipeline()

    if (sslContext.isDefined) {
      pipeline.addLast("ssl", sslContext.get.newHandler(ch.alloc()))
    }

    pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder)
    pipeline.addLast("messageDecoder", new EventBusMessageDecoder)

    pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender())
    pipeline.addLast("messageEncoder", new EventBusMessageEncoder)

    if (inboundHandler.isDefined) {
      pipeline.addLast("handler", inboundHandler.get)
    }
  }
}
