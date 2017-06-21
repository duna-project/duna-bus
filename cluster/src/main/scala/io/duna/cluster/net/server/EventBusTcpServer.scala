package io.duna.cluster.net.server

import cluster.net.server.ServerOptions
import io.duna.cluster.net.SocketChannelInitializer
import io.duna.cluster.net.ssl.SslContext
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{Channel, ChannelFuture, EventLoopGroup}

class EventBusTcpServer(serverOptions: ServerOptions,
                        selectorEventLoop: EventLoopGroup,
                        workerEventLoop: EventLoopGroup)
  extends EventBusServer {

  private var channel: Channel = _

  private val serverHandler = new EventBusServerHandler

  def start(completionListener: => Unit = {}): Unit = {
    val sslContext = SslContext(serverOptions.sslOptions)

    val bootstrap = new ServerBootstrap()
      .group(selectorEventLoop, workerEventLoop)
      .channel(classOf[NioServerSocketChannel])
      .childHandler(new SocketChannelInitializer(sslContext, Some(serverHandler)))

    channel = bootstrap
      .bind(serverOptions.hostname, serverOptions.port)
      .addListener((_: ChannelFuture) => completionListener)
      .channel()
  }

  def stop(completionListener: => Unit = {}): Unit = {
    workerEventLoop.shutdownGracefully()
    selectorEventLoop.shutdownGracefully()

    channel
      .closeFuture()
      .addListener((_: ChannelFuture) => completionListener)
      .await(EventBusTcpServer.SEVER_CLOSE_TIMEOUT)
  }
}

object EventBusTcpServer {
  private val SEVER_CLOSE_TIMEOUT: Long = 5000L

  def apply(serverOptions: ServerOptions, selectorEventLoop: EventLoopGroup, workerEventLoop: EventLoopGroup)
  : EventBusTcpServer = new EventBusTcpServer(serverOptions, selectorEventLoop, workerEventLoop)
}
