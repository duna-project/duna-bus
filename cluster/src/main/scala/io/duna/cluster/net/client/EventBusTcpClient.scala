package io.duna.cluster.net.client

import java.net.InetSocketAddress

import io.duna.cluster.config.{ClientOptions, SslOptions}
import io.duna.cluster.net.SocketChannelInitializer
import io.duna.cluster.net.ssl.SslContext
import io.netty.bootstrap.Bootstrap
import io.netty.channel.pool.{AbstractChannelPoolMap, ChannelPool, FixedChannelPool}
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{Channel, EventLoopGroup}
import io.netty.util.concurrent.Future

class EventBusTcpClient(clientOptions: ClientOptions,
                        sslOptions: Option[SslOptions],
                        eventLoopGroup: EventLoopGroup)
  extends EventBusClient {

  private val sslContext =
    if (sslOptions.isDefined)
      SslContext.forServer(sslOptions.get)
    else None

  private val bootstrap = new Bootstrap()
    .group(eventLoopGroup)
    .channel(classOf[NioSocketChannel])
    .handler(new SocketChannelInitializer(sslContext))

  private val channelPoolMap = new AbstractChannelPoolMap[InetSocketAddress, ChannelPool] {
    override def newPool(key: InetSocketAddress): ChannelPool = {
      new FixedChannelPool(bootstrap.remoteAddress(key), new ClientChannelPoolHandler, 5)
    }
  }

  override def connectTo(address: InetSocketAddress)(onConnect: (Channel) => Unit): Unit = {
    val pool = channelPoolMap.get(address)
    pool.acquire()
      .addListener((future: Future[Channel]) => {
        if (future.isSuccess) {
          val channel = future.getNow

          onConnect(channel)
          pool.release(channel)
        }
      })
  }
}

object EventBusTcpClient {
  def apply(clientOptions: ClientOptions,
            sslOptions: Option[SslOptions],
            eventLoopGroup: EventLoopGroup): EventBusTcpClient =
    new EventBusTcpClient(clientOptions, sslOptions, eventLoopGroup)
}