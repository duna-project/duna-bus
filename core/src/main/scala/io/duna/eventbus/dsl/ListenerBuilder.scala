package io.duna.eventbus.dsl

import scala.reflect.runtime.universe.TypeTag

import io.duna.eventbus.EventBus
import io.duna.eventbus.event.Listener

/** Builder used to bind a listener to an event.
  *
  * @param listener the listener that will react to an event.
  * @param eventBus the event bus.
  */
class ListenerBuilder[A: TypeTag](protected val listener: Listener[A])
                                 (implicit eventBus: EventBus) {

  private var onlyOnce = false

  /** This listener will react only once to the event. */
  def only(o: once.type): ListenerBuilder[A] = {
    onlyOnce = true
    this
  }

  /** Registers this listener to the event provided.
    *
    * @param event the event to be listened to.
    */
  def to(event: String): Listener[A] = {
    if (onlyOnce) eventBus route event only once to listener
    else eventBus route event to listener

    listener
  }
}

object ListenerBuilder {
  def apply[T: TypeTag](listener: Listener[T])(implicit eventBus: EventBus) =
    new ListenerBuilder[T](listener)
}
