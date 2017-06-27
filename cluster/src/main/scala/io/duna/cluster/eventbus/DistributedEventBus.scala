package io.duna.cluster.eventbus

import scala.concurrent.Future
import scala.reflect.runtime.universe

import io.duna.cluster.config.EventBusOptions
import io.duna.cluster.eventbus.message.ClusteredPostman
import io.duna.cluster.eventbus.node.NodeRegistry
import io.duna.cluster.net.client.EventBusTcpClient
import io.duna.cluster.net.server.tcp.EventBusTcpServer
import io.duna.eventbus.SingleNodeEventBus
import io.duna.eventbus.event.Listener
import io.duna.eventbus.message.Postman
import io.duna.eventbus.routing.{Route, Router}
import io.duna.types.DefaultsTo
import io.netty.channel.DefaultEventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup

class DistributedEventBus(options: EventBusOptions)
  extends SingleNodeEventBus(new DefaultEventLoopGroup(options.threadingOptions.eventBusThreads)) {

  /** The [EventLoopGroup] used by the server to listen for new connections. */
  private val acceptorEventLoopGroup = new NioEventLoopGroup(options.threadingOptions.serverAcceptorThreads)

  /** The [EventLoopGroup] used by the server and client to handle I/O. */
  private val workersEventLoopGroup = new NioEventLoopGroup(options.threadingOptions.workerThreads)

  private val client = EventBusTcpClient(options.clientOptions, options.sslOptions, workersEventLoopGroup)

  private val server = EventBusTcpServer(
    options.serverOptions,
    options.sslOptions,
    acceptorEventLoopGroup,
    workersEventLoopGroup)

  private val nodeRegistry: NodeRegistry = null

  override protected[this] val postman: Postman = new ClusteredPostman(client, null)

  override protected[this] val router: Router = null

  server.start()

  override def route[T: universe.TypeTag](event: String)(implicit default: DefaultsTo[T, Unit]): Route[T] = {
    super.route(event)
  }

  override def unroute(event: String, listener: Listener[_]): Future[Listener[_]] = {
    super.unroute(event, listener)
  }
}
