package io.duna.dsl

import scala.reflect.runtime.universe.TypeTag

import io.duna.eventbus.EventBus
import io.duna.eventbus.event.Listener
import io.duna.eventbus.message.Signal

class DslListener[A: TypeTag, B <: AnyRef : TypeTag](event: String, onlyOnce: Boolean)
                                         (implicit eventBus: EventBus) {

  private var onNextHandler: (Option[A]) => B = _
  private var onErrorHandler: (Throwable) => Unit = { _ => }
  private var onSignalHandler: PartialFunction[Signal, Unit] = {
    case _ =>
  }

  private val listener: Listener[A, Option[B]] = new Listener[A, Option[B]]() {
    override def onNext(value: Option[_ <: A]): Option[B] =
      if (onNextHandler != null) Some(onNextHandler(value))
      else None

    override def onSignal(signal: Signal): Unit = onSignalHandler(signal)

    override def onError(error: Throwable): Unit = onErrorHandler(error)
  }

  if (onlyOnce) eventBus route[A] event only once to listener
  else eventBus route[A] event to listener

  def onNext(handler: (Option[A]) => B): DslListener[A, B] = {
    onNextHandler = handler
    this
  }

  def onNext(handler: => B): DslListener[A, B] = {
    onNextHandler = { _ => handler }
    this
  }

  def onError(handler: (Throwable) => Unit): DslListener[A, B] = {
    onErrorHandler = handler
    this
  }

  def onSignal(handler: PartialFunction[Signal, Unit]): DslListener[A, B] = {
    onSignalHandler = handler
    this
  }
}
