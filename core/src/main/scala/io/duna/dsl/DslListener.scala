package io.duna.dsl

import scala.reflect.runtime.universe.TypeTag

import io.duna.concurrent.future.Future
import io.duna.eventbus.EventBus
import io.duna.eventbus.event.Listener
import io.duna.eventbus.message.Signal
import io.duna.types.DefaultsTo

class DslListener[A: TypeTag](event: String, onlyOnce: Boolean)
                             (implicit eventBus: EventBus) {

  @volatile private var onNextHandler: (Option[A]) => Unit = _
  @volatile private var onErrorHandler: (Throwable) => Unit = { _ => }
  @volatile private var onSignalHandler: PartialFunction[Signal, Unit] = {
    case _ =>
  }

  private[dsl] val listener: Listener[A] = new Listener[A]() {

    if (onlyOnce) listen only once to event
    else listen to event

    override def onNext(value: Option[_ <: A]): Unit =
      if (onNextHandler != null) Some(onNextHandler(value))
      else None

    override def onSignal(signal: Signal): Unit = onSignalHandler(signal)

    override def onError(error: Throwable): Unit = onErrorHandler(error)
  }

  def onNext(handler: (Option[A]) => Unit): DslListener[A] = {
    onNextHandler = handler
    this
  }

  def onNext(handler: => Unit): DslListener[A] = {
    onNextHandler = { _ => handler }
    this
  }

  def onError(handler: (Throwable) => Unit): DslListener[A] = {
    onErrorHandler = handler
    this
  }

  def onSignal(handler: PartialFunction[Signal, Unit]): DslListener[A] = {
    onSignalHandler = handler
    this
  }
}

class DslListenerBuilder(implicit eventBus: EventBus) {

  private var onlyOnce = false

  def only(o: once.type): DslListenerBuilder = {
    onlyOnce = true
    this
  }

  def to[A: TypeTag](event: String)(implicit eventBus: EventBus): DslListener[A] =
    new DslListener[A](event, onlyOnce)

}

class DslListenerRemoval(listener: Listener[_])(implicit eventBus: EventBus) {

  def from(event: String): Future[Listener[_]] = {
    require(event != null)
    eventBus unroute (event, listener)
  }
}