package io.duna.eventbus.event

import java.util.UUID

import scala.reflect.runtime.universe.TypeTag

import io.duna.eventbus.{Context, EventBus}

class DefaultListener[T: TypeTag](implicit eventBus: EventBus)
  extends Listener[T] {

  override val listenerId: String = UUID.randomUUID().toString

  private[this] var onNextHandler: (Option[T]) => Unit = { _ => }
  private[this] var onErrorHandler: PartialFunction[Throwable, Unit] = {
    case _ =>
  }
  private[this] var onCompleteHandler: () => Unit = { () =>
    eventBus unroute(Context().currentEvent, this)
  }

  override def onNext(value: Option[T]): Unit = onNextHandler(value)

  override def onError(error: Throwable): Unit = onErrorHandler(error)

  override def onComplete(): Unit = onCompleteHandler()

  final def onNext(handler: Option[T] => Unit): DefaultListener[T] = {
    require(handler != null, "The handler cannot be null.")

    onNextHandler = handler
    this
  }

  final def onNext(handler: => Unit): DefaultListener[T] = {
    onNextHandler = { _ => handler }
    this
  }

  final def onError(handler: PartialFunction[Throwable, Unit]): DefaultListener[T] = {
    require(handler != null, "The handler cannot be null.")
    onErrorHandler = handler

    this
  }

  final def onComplete(handler: => Unit): DefaultListener[T] = {
    onCompleteHandler = () => {
      handler
      eventBus unroute(Context().currentEvent, this)
    }

    this
  }
}

object DefaultListener {
  def apply[T: TypeTag]()(implicit eventBus: EventBus): DefaultListener[T] = new DefaultListener[T]()

  def listenTo[T: TypeTag](event: String)(implicit eventBus: EventBus): DefaultListener[T] =
    new DefaultListener[T] {
      this: Listener[T] =>
        listen to event
    }

  def listenOnceTo[T: TypeTag](event: String)(implicit eventBus: EventBus): DefaultListener[T] =
    new DefaultListener[T] {
      listen only once to event
    }
}
