package io.duna.eventbus

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

import io.duna.eventbus.message.Message
import io.duna.eventbus.routing.Router
import org.jctools.queues.atomic.MpmcAtomicArrayQueue

protected case class DefaultListener[T: ClassTag](override val event: String)
                                                 (implicit val router: Router,
                                                  executionContext: ExecutionContext)
  extends Listener[T] {

  private lazy val messageType = implicitly[ClassTag[T]].runtimeClass

  private lazy val messageQueue = new MpmcAtomicArrayQueue[Message[T]](DefaultListener.MAX_PENDING_MESSAGES)
  private lazy val errorQueue = new MpmcAtomicArrayQueue[Message[Throwable]](DefaultListener.MAX_PENDING_MESSAGES)

  private var messageHandler: (Option[T]) => Unit = _
  private var errorHandler: (Throwable) => Unit = _

  private var matcher: (Option[_], Context) => Boolean = { (_, _) => true }

  override def onReceive(handler: (Option[T]) => Unit): Listener[T] = {
    if (handler == null) throw new NullPointerException

    messageHandler = handler
    while (!messageQueue.isEmpty) processMessage()

    this
  }

  override def onError(handler: (Throwable) => Unit): Listener[T] = {
    if (handler == null) throw new NullPointerException

    errorHandler = handler
    while (!errorQueue.isEmpty) processError()

    this
  }

  override def when(matcher: (Option[_], Context) => Boolean): Listener[T] = {
    if (matcher == null) throw new NullPointerException
    this.matcher = matcher
    this
  }

  override def stop(): Unit = ???

  override private[eventbus] def next(message: Message[_]) = {
    message.attachmentType match {
      case c if messageType.isAssignableFrom(c) =>
        messageQueue.offer(message.asInstanceOf[Message[T]])
        processMessage()
      case c if classOf[Throwable].isAssignableFrom(c) && message.attachment.isDefined =>
        errorQueue.offer(message.asInstanceOf[Message[Throwable]])
        processError()
      case _ =>
        errorQueue.offer(message.copyAsErrorMessage(
          new IllegalArgumentException("Incompatible attachment type for this listener.")))
        processError()
    }
  }

  override private[eventbus] def matches(message: Message[_]) =
    matcher(message.attachment, Context createFrom message)

  private[this] def processMessage(): Unit = {
    val currentHandler = messageHandler

    if (currentHandler != null) {
      val message = messageQueue.poll()
      if (message != null) currentHandler(message.attachment)
    }
  }

  private[this] def processError(): Unit = {
    val currentHandler = errorHandler

    if (currentHandler != null) {
      val message = errorQueue.poll()
      if (message != null) currentHandler(message.attachment.get)
    }
  }
}

object DefaultListener {
  private val MAX_PENDING_MESSAGES = 16
}
