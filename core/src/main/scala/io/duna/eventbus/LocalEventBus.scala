package io.duna.eventbus

import java.util.concurrent.{ArrayBlockingQueue, ExecutorService}
import scala.annotation.tailrec
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag
import scala.util.Try

import io.duna.eventbus.message.{Message, Postman}
import io.duna.eventbus.routing.Router

class LocalEventBus(private val workerPool: ExecutorService)
                   (implicit executionContext: ExecutionContext)
  extends EventBus with Runnable {

  implicit val router = new Router
  implicit val context = Context()

  private var shouldShutdown = false
  private val selectorThread = new Thread(this)

  private val messageQueue = new ArrayBlockingQueue[Message[_]](1024)

  private val postman = new Postman {
    override def deliver(message: Message[_]): Unit = {
      LocalEventBus.this.consume(message)
    }
  }

  override def emit[T: ClassTag](name: String): Emitter[T] = {
    implicit val sourceEvent = Try(Context().currentEvent).toOption
    new DefaultEmitter[T](name, postman)
  }

  override def listenTo[T: ClassTag](event: String): Listener[T] = {
    val listener = new DefaultListener[T](event)
    router route event to listener
    listener
  }

  override def listenOnceTo[T: ClassTag](event: String): Listener[T] = {
    val listener = new DefaultListener[T](event)
    router route event onceTo listener
    listener
  }

  override def remove(listener: Listener[_]): Unit = router -= listener

  override def removeAll(event: String): Unit = router --= event

  override def respondWith[T: ClassTag](attachment: Option[T]): Unit = ???

  override def consume(message: Message[_]): Unit = {
    messageQueue.offer(message)
  }

  def start(): Unit = if (!shouldShutdown) selectorThread.start()

  def shutdown(): Unit = {
    shouldShutdown = true
    messageQueue.offer(Message[Nothing](target = null))
  }

  @tailrec override final def run(): Unit = {
    val message = Try(scala.concurrent.blocking(messageQueue.poll()))
      .toOption
      .orNull

    if (message != null && message.target != null) {
      router matching message foreach { listener =>
        executionContext execute { () =>
          Context createFrom(message, this) assign()
          listener next message
        }
      }
    }

    if (!shouldShutdown)
      run()
  }
}
