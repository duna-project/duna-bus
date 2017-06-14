package io.duna.cluster.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel

class EventBusServer {

  var channel: Channel = _

  val selectorGroup = new NioEventLoopGroup()
  val workerGroup = new NioEventLoopGroup()

  def start(): Unit = {
    val bootstrap = new ServerBootstrap()
      .group(selectorGroup, workerGroup)
      .channel(classOf[NioServerSocketChannel])
      .childHandler(new ServerChannelInitializer)

    channel = bootstrap.bind(7888).sync().channel()
  }

  def stop(): Unit = {
    workerGroup.shutdownGracefully()
    selectorGroup.shutdownGracefully()

    channel.closeFuture().sync()
  }
}
