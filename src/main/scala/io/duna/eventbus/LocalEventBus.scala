package io.duna.eventbus

import java.util.concurrent.{ConcurrentHashMap, ExecutorService}

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

import io.duna.concurrent.EventLoop
import io.duna.eventbus.messaging.{Message, MessageConsumer, MessageDispatcher}

class LocalEventBus(private val eventLoopGroup: EventLoopGroup,
                    private val workerPool: ExecutorService)
  extends EventBus {

  private val subscribers = new ConcurrentHashMap[String, MessageConsumer[_]]().asScala

  private val dispatcher = new MessageDispatcher[Any] {
    override def dispatch(message: Message[Any]): Unit = {
      // LocalEventBus.this.routeEventMessage(message)
    }
  }

  override def emit[T](event: String): Emitter[T] = ???

  override def subscribeTo[T](event: String): Subscriber[T] = ???

  override def unsubscribe(subscriber: Subscriber[_]): Unit = ???

  override def unsubscribeAll(event: String): Unit = ???

  override def consume(message: Message[_]): Unit = ???
}
