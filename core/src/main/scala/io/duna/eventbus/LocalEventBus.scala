package io.duna.eventbus

import java.util.concurrent.{ExecutorService, PriorityBlockingQueue}
import scala.annotation.tailrec

import io.duna.concurrent.EventLoopGroup
import io.duna.eventbus.messaging.{Message, MessageDispatcher}
import io.duna.eventbus.routing.Router
import org.jctools.queues.atomic.MpscLinkedAtomicQueue

class LocalEventBus(private val eventLoopGroup: EventLoopGroup,
                    private val workerPool: ExecutorService)
  extends EventBus with Runnable {

  val routes = new Router

  private val messageQueue = new MpscLinkedAtomicQueue[Message[_]]

  private val dispatcher = new MessageDispatcher {
    override def dispatch(message: Message[_]): Unit = {
      LocalEventBus.this.consume(message)
    }
  }

  override def emit[T](event: String): Emitter[T] = new DefaultEmitter[T](event, dispatcher)

  override def subscribeTo[T](event: String): Subscriber[T] = {
    val subscriber = new DefaultSubscriber[T](event, routes)
    subscriber
  }

  override def unsubscribe(subscriber: Subscriber[_]): Unit = subscriber.unsubscribe()

  override def unsubscribeAll(event: String): Unit = routes /-> event

  override def consume(message: Message[_]): Unit = {
    // This function can be used later for event sourcing
    messageQueue.offer(message)
  }

  @tailrec override final def run(): Unit = {
    val nextMessage = messageQueue.poll()

    routes matching nextMessage dispatch { route =>
      eventLoopGroup.submit { () =>
        if (!route(nextMessage))
          messageQueue.offer(nextMessage)
      }
    }

    run()
  }
}
