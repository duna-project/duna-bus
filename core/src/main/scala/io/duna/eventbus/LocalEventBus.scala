package io.duna.eventbus

import java.util.concurrent.{ArrayBlockingQueue, ExecutorService}

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag
import scala.util.Try
import scala.util.control.NonFatal

import io.duna.eventbus.errors.NoRouteFoundException
import io.duna.eventbus.message.{Message, Postman}
import io.duna.eventbus.routing.Router

class LocalEventBus(private val workerPool: ExecutorService)
                   (implicit executionContext: ExecutionContext)
  extends EventBus with Runnable {

  implicit val router = new Router
  implicit val context = Context()

  private var shouldShutdown = false
  private val selectorThread = new Thread(this)

  private var errorHandler: Throwable => Unit = { _ => }

  private val messageQueue = new ArrayBlockingQueue[Message[_]](1024)

  private val postman = new Postman {
    override def deliver(message: Message[_]): Unit = {
      LocalEventBus.this.consume(message)
    }
  }

  override def emit[T: ClassTag](name: String): Emitter[T] = {
    new DefaultEmitter[T](name, Try(Context().currentEvent).toOption, postman)
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

  override def onError(handler: (Throwable) => Unit): Unit = {
    if (handler == null) throw new NullPointerException
    this.errorHandler = handler
  }

  override def consume(message: Message[_]): Unit = {
    messageQueue.offer(message)
  }

  def start(): Unit = if (!shouldShutdown) selectorThread.start()

  def shutdown(): Unit = {
    shouldShutdown = true
    messageQueue.offer(Message(target = null))
  }

  @tailrec override final def run(): Unit = {
    val message = Try(scala.concurrent.blocking(messageQueue.poll()))
      .toOption
      .orNull

    if (message != null && message.target != null) {
      val matching = router matching message

      if (matching.isEmpty) {
        errorHandler(NoRouteFoundException(s"No routes matching ${message.target} were found."))
      } else {
        matching foreach { listener =>
          executionContext execute { () =>
            Context createFrom(message, this) assign()
            try {
              listener next message
            } catch {
              case NonFatal(e) =>
                val errorMessage = message.copyAsErrorMessage(e)(ClassTag(e.getClass))
                listener next errorMessage
            }
          }
        }
      }
    }

    if (!shouldShutdown)
      run()
  }
}
