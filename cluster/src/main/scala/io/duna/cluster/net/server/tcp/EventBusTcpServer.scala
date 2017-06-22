package io.duna.cluster.net.server.tcp

import io.duna.cluster.config.{ServerOptions, SslOptions}
import io.duna.cluster.net.SocketChannelInitializer
import io.duna.cluster.net.server.{EventBusServer, EventBusServerHandler}
import io.duna.cluster.net.ssl.SslContext
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{Channel, ChannelFuture, EventLoopGroup}

class EventBusTcpServer(serverOptions: ServerOptions,
                        sslOptions: Option[SslOptions],
                        selectorEventLoop: EventLoopGroup,
                        workerEventLoop: EventLoopGroup)
  extends EventBusServer {

  private var channel: Channel = _

  private val serverHandler = new EventBusServerHandler

  def start(completionListener: => Unit = {}): Unit = {
    val sslContext =
      if (sslOptions.isDefined)
        SslContext.forServer(sslOptions.get)
      else None

    val bootstrap = new ServerBootstrap()
      .group(selectorEventLoop, workerEventLoop)
      .channel(classOf[NioServerSocketChannel])
      .childHandler(new SocketChannelInitializer(sslContext, Some(serverHandler)))

    channel = bootstrap
      .bind(serverOptions.host, serverOptions.port)
      .addListener((_: ChannelFuture) => completionListener)
      .channel()
  }

  def stop(completionListener: => Unit = {}): Unit = {
    channel
      .closeFuture()
      .addListener((_: ChannelFuture) => completionListener)
      .await(EventBusTcpServer.SEVER_CLOSE_TIMEOUT)
  }
}

object EventBusTcpServer {
  private val SEVER_CLOSE_TIMEOUT: Long = 5000L

  def apply(serverOptions: ServerOptions,
            sslOptions: Option[SslOptions],
            selectorEventLoop: EventLoopGroup,
            workerEventLoop: EventLoopGroup): EventBusTcpServer =
    new EventBusTcpServer(serverOptions, sslOptions, selectorEventLoop, workerEventLoop)
}
