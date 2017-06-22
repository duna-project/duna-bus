package io.duna.cluster.net.client

import io.netty.channel.Channel
import io.netty.channel.pool.AbstractChannelPoolHandler

class ClientChannelPoolHandler extends AbstractChannelPoolHandler {
  override def channelCreated(ch: Channel): Unit = {}
}
