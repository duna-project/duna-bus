package io.duna.eventbus

import java.util.concurrent.atomic.AtomicReference

import scala.reflect.ClassTag

import io.duna.eventbus.messaging.MessageConsumer

protected class SubscriberImpl[T: ClassTag](override val event: String,
                                            private val dispatcher: MessageConsumer[T])
  extends Subscriber[T] {

  val lastMessageHandler = new AtomicReference[Option[T] => Unit](null)
  val lastErrorHandler = new AtomicReference[(Exception) => Unit](null)

  override def onReceive(handler: (Option[T]) => Unit): Subscriber[T] = {
    if (!lastMessageHandler.compareAndSet(null, handler)) {
      val old = lastMessageHandler.get()
      dispatcher.removeMessageHandler(old)
      lastMessageHandler.compareAndSet(old, handler)
    }

    dispatcher.addMessageHandler(handler)

    this
  }

  override def onError(handler: (Exception) => Unit): Subscriber[T] = {
    if (!lastErrorHandler.compareAndSet(null, handler)) {
      val old = lastErrorHandler.get()
      dispatcher.removeErrorHandler(old)
      lastErrorHandler.compareAndSet(old, handler)
    }

    dispatcher.addErrorHandler(handler)

    this
  }
}
