package io.duna

import scala.reflect.runtime.universe.TypeTag

import io.duna.eventbus.{Context, EventBus}

package object dsl {

  object once

  @inline
  def listen(implicit eventBus: EventBus): ListenerBuilder = new ListenerBuilder

  @inline
  def emit(implicit eventBus: EventBus): DslEmitter = new DslEmitter

  def reply[A: TypeTag](value: Option[A])(implicit eventBus: EventBus): Unit = {
    Context().replyTo match {
      case Some(e) => emit event e send[A] value
      case None =>
    }
  }
}
