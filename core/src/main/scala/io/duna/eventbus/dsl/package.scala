package io.duna.eventbus

import scala.reflect.runtime.universe.TypeTag

import io.duna.eventbus.event.Listener

package object dsl {

  object once {}

  object to {}

  @inline
  def listen(implicit eventBus: EventBus): DslListenerBuilder =
    new DslListenerBuilder

  @inline
  def emit(implicit eventBus: EventBus): DslEmitterBuilder =
    new DslEmitterBuilder

  @inline
  def reply[T: TypeTag](attachment: Option[T] = None)
                        (implicit eventBus: EventBus): Unit = {
    Context.current.replyTo match {
      case Some(replyEvent) =>
        eventBus.emit(replyEvent).send(attachment)
      case None =>
        throw new RuntimeException("No reply event defined in the current context.")
    }
  }
}
