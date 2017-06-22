package io.duna.cluster.eventbus.message

import io.duna.cluster.eventbus.node.NodeRegistry
import io.duna.cluster.net.client.EventBusClient
import io.duna.eventbus.message.{Broadcast, Message, Postman, Unicast}

class ClusteredPostman(client: EventBusClient,
                       nodeRegistry: NodeRegistry) extends Postman {

  override def deliver(message: Message[_]): Unit = {
    message.transmissionMode match {
      case Unicast =>
        val node = nodeRegistry.next(message.target)(message.typeTag)
        client.connectTo(node)(_.writeAndFlush(message))
      case Broadcast =>
        nodeRegistry
          .list(message.target)(message.typeTag)
          .foreach(client.connectTo(_)(_.writeAndFlush(message)))
    }
  }
}
