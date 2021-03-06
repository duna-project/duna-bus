package io.duna.eventbus.event

import java.util.UUID

import scala.reflect.runtime.universe.TypeTag

import io.duna.dsl.{DslListener, DslListenerBuilder, once}
import io.duna.eventbus.message.{Completion, Signal}
import io.duna.eventbus.{Context, EventBus}

/** Represents an event listener.
  *
  * @tparam A the type of values processed by this listener.
  */
abstract class Listener[A: TypeTag](implicit eventBus: EventBus) {

  val messageType: TypeTag[A] = implicitly[TypeTag[A]]

  /** Process the next event value emitted by the [[EventBus]].
    *
    * @param value the value emitted.
    */
  def onNext(value: Option[_ <: A] = None): Unit

  /** Process an event error emitted by the [[EventBus]].
    *
    * @param error the error emitted.
    */
  def onError(error: Throwable): Unit = {}

  /** Process signals received from the [[EventBus]].
    *
    * @param signal the signal received.
    */
  def onSignal(signal: Signal): Unit = signal.signalType match {
    case _: Completion.type => onComplete()
    case _ =>
  }

  /** Finishes the processing of the event emitted by the [[EventBus]]. */
  def onComplete(): Unit = {}

  /** The current event context. */
  def context: Context = Context.current

  private[eventbus] final val listenerId: String = UUID.randomUUID().toString

  private[eventbus] final val executor = eventBus.eventLoopGroup.next()

  protected final val listen = new ListenerBuilder(this)

}

class ListenerBuilder[A: TypeTag](listener: Listener[A])
                                 (implicit eventBus: EventBus) {

  private var onlyOnce = false

  def only(o: once.type): ListenerBuilder[A] = {
    onlyOnce = true
    this
  }

  def to(event: String)(implicit eventBus: EventBus): Unit =
    eventBus route[A] event to listener

}