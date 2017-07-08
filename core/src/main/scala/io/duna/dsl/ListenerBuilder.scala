package io.duna.dsl

import scala.reflect.runtime.universe.TypeTag

import io.duna.eventbus.EventBus
import io.duna.types.DefaultsTo

class ListenerBuilder(implicit eventBus: EventBus) {

  private var onlyOnce = false

  def only(o: once.type): ListenerBuilder = {
    onlyOnce = true
    this
  }

  def to[A: TypeTag, B: TypeTag](event: String)(implicit eventBus: EventBus,
                                                default: B DefaultsTo Unit): DslListener[A, B] =
    new DslListener[A, B](event, onlyOnce)

}
