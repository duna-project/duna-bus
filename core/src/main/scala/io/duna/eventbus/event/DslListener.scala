package io.duna.eventbus.event

import java.util.UUID

import scala.reflect.runtime.universe.TypeTag

import io.duna.eventbus.dsl.once
import io.duna.eventbus.{Context, EventBus}

class DslListener[T: TypeTag](implicit eventBus: EventBus) {

  private[eventbus] val listener = new Listener[T] {

    override val listenerId: String = UUID.randomUUID().toString

    override def onNext(value: Option[T]): Unit = onNextHandler(value)

    override def onError(error: Throwable): Unit = onErrorHandler(error)

    override def onComplete(): Unit = onCompleteHandler()
  }

  private[this] var onNextHandler: (Option[T]) => Unit = { _ => }
  private[this] var onErrorHandler: PartialFunction[Throwable, Unit] = { case _ => }

  private[this] var onCompleteHandler: () => Unit = { () =>
    eventBus unroute(Context().currentEvent, listener)
  }

  def onNext(handler: Option[T] => Unit): DslListener[T] = {
    require(handler != null, "The handler cannot be null.")
    onNextHandler = handler

    this
  }

  def onNext(handler: => Unit): DslListener[T] = {
    onNextHandler = { _ => handler }

    this
  }

  def onException(handler: PartialFunction[Throwable, Unit]): DslListener[T] = {
    require(handler != null, "The handler cannot be null.")
    onErrorHandler = handler

    this
  }

  def onComplete(handler: => Unit): DslListener[T] = {
    onCompleteHandler = () => {
      eventBus unroute (Context().currentEvent, listener) onSuccess { _ =>
        handler
      }
    }

    this
  }
}

object DslListener {
  def apply[T: TypeTag]()(implicit eventBus: EventBus): DslListener[T] = new DslListener[T]()

  def listenTo[T: TypeTag](event: String)(implicit eventBus: EventBus): DslListener[T] = {
    val dslListener = new DslListener[T]
    dslListener.listener.listen to event

    dslListener
  }

  def listenOnceTo[T: TypeTag](event: String)(implicit eventBus: EventBus): DslListener[T] ={
    val dslListener = new DslListener[T]
    dslListener.listener.listen only once to event

    dslListener
  }
}
