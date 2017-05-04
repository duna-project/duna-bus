package io.duna.eventbus

import java.util.concurrent.{ConcurrentHashMap, ExecutorService}
import scala.collection.JavaConverters._

import io.duna.concurrent.EventLoopGroup
import io.duna.eventbus.messaging.{Message, MessageDispatcher}

class LocalEventBus(private val eventLoopGroup: EventLoopGroup,
                    private val workerPool: ExecutorService)
  extends EventBus {

  private val eventRoutes = new ConcurrentHashMap[String, Router]().asScala

  private val dispatcher = new MessageDispatcher[Any] {
    override def dispatch(message: Message[Any]): Unit = {
      // LocalEventBus.this.routeEventMessage(message)
    }
  }

  override def emit[T](event: String): Emitter[T] = ???

  override def subscribeTo[T](event: String): Subscriber[T] = {
    val router = eventRoutes.getOrElseUpdate(event, new Router)
    // val subscriber = new DefaultSubscriber[T](this)

  }

  override def unsubscribe(subscriber: Subscriber[_]): Unit = ???

  override def unsubscribeAll(event: String): Unit = ???

  override def consume(message: Message[_]): Unit = ???
}
