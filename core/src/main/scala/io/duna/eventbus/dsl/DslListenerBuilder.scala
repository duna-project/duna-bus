package io.duna.eventbus.dsl

import scala.reflect.runtime.universe.TypeTag

import io.duna.eventbus.EventBus
import io.duna.eventbus.event.DefaultListener
import io.duna.types.DefaultsTo

class DslListenerBuilder {

  private var onlyOnce = false

  /** This listener will react only once to the event. */
  def only(o: once.type): DslListenerBuilder = {
    onlyOnce = true
    this
  }

  def to[T: TypeTag](event: String)
                     (implicit default: T DefaultsTo Unit,
                      eventBus: EventBus): DefaultListener[T] = {
    if (onlyOnce)
      DefaultListener.listenTo[T](event)
    else
      DefaultListener.listenOnceTo[T](event)
  }
}
