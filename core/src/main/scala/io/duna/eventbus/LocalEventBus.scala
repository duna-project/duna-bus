package io.duna.eventbus

import java.util.concurrent.{ArrayBlockingQueue, ConcurrentHashMap, ExecutorService}

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

import io.duna.eventbus.message.{Message, Messenger}
import io.duna.eventbus.routing.Router
import org.jctools.queues.atomic.MpscLinkedAtomicQueue

class LocalEventBus(private val workerPool: ExecutorService)
                   (implicit executionContext: ExecutionContext)
  extends EventBus with Runnable {

  implicit val routes = new Router
  implicit val context = Context()

  private val selectorThread = new Thread(this)
  private var shouldShutdown = false

  private val messageQueue = new ArrayBlockingQueue[Message[_]](1024)

  private val dispatcher = new Messenger {
    override def send(message: Message[_]): Unit = {
      LocalEventBus.this.consume(message)
    }
  }

  override def emit[T: ClassTag](name: String): Emitter[T] = {
    new DefaultEmitter[T](name, dispatcher)
  }

  override def listenTo[T: ClassTag](event: String): Listener[T] = {
    val subscriber = new DefaultListener[T](event, routes)
    subscriber
  }

  override def unsubscribe(subscriber: Listener[_]): Unit = subscriber.stop()

  override def unsubscribeAll(event: String): Unit = routes -/> event

  override def consume(message: Message[_]): Unit = {
    messageQueue.offer(message)
  }

  def start(): Unit = if (!selectorThread.isAlive) selectorThread.start()

  def stop(): Unit = shouldShutdown = true

  @tailrec override final def run(): Unit = {
    val nextMessage = messageQueue.poll()

    if (nextMessage != null) {
      routes matching nextMessage foreach { listener =>
        executionContext execute { () =>
          implicit val context = new Context

          Try(listener nextValue nextMessage.attachment) match {
            case Success(_) =>
            case Failure(e) =>
              val ctag = ClassTag[Throwable](e.getClass)
              messageQueue.offer(nextMessage.copyAsErrorMessage(e)(ctag))
          }
        }
      }
    }

    if (!shouldShutdown)
      run()
  }
}
