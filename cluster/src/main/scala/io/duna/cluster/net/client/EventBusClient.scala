package io.duna.cluster.net.client

import java.net.InetSocketAddress

import io.netty.channel.Channel

trait EventBusClient {

  def connectTo(address: InetSocketAddress)(onConnect: Channel => Unit): Unit
}
