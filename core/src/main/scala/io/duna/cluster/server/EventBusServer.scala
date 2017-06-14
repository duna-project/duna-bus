package io.duna.cluster.server

import com.twitter.util.Future
import io.duna.cluster.channel.EventBusChannelInitializer
import io.duna.cluster.ssl.SslContext
import io.netty.bootstrap.{Bootstrap, ServerBootstrap}
import io.netty.channel.Channel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.{NioDatagramChannel, NioServerSocketChannel}

class EventBusServer(serverOptions: ServerOptions) {

  private var channel: Channel = _

  private lazy val selectorGroup = new NioEventLoopGroup()
  private lazy val workerGroup = new NioEventLoopGroup()

  def start(): Unit = {
    val sslContext = SslContext(serverOptions.sslOptions)

  }

  def stop(): Unit = {
    workerGroup.shutdownGracefully()
    selectorGroup.shutdownGracefully()
  }
}
