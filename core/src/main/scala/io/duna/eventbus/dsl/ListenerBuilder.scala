package io.duna.eventbus.dsl

import scala.reflect.ClassTag

import io.duna.eventbus.EventBus
import io.duna.eventbus.event.Listener

/** Builder used to bind a listener to an event.
  *
  * @param listener the listener that will react to an event.
  * @param eventBus the event bus.
  */
class ListenerBuilder[T: ClassTag](protected val listener: Listener[T])
                                  (implicit eventBus: EventBus) {

  private var onlyOnce = false

  /** This listener will react only once to the event. */
  def only(o: once.type): ListenerBuilder[T] = {
    onlyOnce = true
    this
  }

  /** Registers this listener to the event provided.
    *
    * @param event the event to be listened to.
    */
  def to(event: String): Listener[T] = {
    if (onlyOnce) eventBus route event only once to listener
    else eventBus route event to listener

    listener
  }
}

object ListenerBuilder {
  def apply[T: ClassTag](listener: Listener[T])(implicit eventBus: EventBus) =
    new ListenerBuilder[T](listener)
}
