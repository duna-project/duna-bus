package io.duna.cluster.net.server

import io.duna.eventbus.message.Message
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}

class EventBusServerHandler extends SimpleChannelInboundHandler[Message[_]] {

  override def channelRead0(ctx: ChannelHandlerContext, msg: Message[_]): Unit = ???
}
