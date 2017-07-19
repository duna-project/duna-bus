package io.duna

import scala.reflect.runtime.universe.TypeTag

import io.duna.eventbus.event.Listener
import io.duna.eventbus.{Context, EventBus}

package object dsl {

  object once

  @inline
  def listen(implicit eventBus: EventBus): DslListenerBuilder = new DslListenerBuilder

  @inline
  def remove(l: Listener[_])(implicit eventBus: EventBus) = new DslListenerRemoval(l)

  @inline
  def remove(l: DslListener[_])(implicit eventBus: EventBus) = new DslListenerRemoval(l.listener)

  @inline
  def emit(implicit eventBus: EventBus): DslEmitter = new DslEmitter

  def replyWith[A: TypeTag](value: Option[A])(implicit eventBus: EventBus): Unit = {
    Context().replyEvent match {
      case Some(e) => emit event e send[A] value
      case None =>
    }
  }
}
