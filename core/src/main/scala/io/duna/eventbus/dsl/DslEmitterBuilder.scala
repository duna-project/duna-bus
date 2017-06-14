package io.duna.eventbus.dsl

import scala.reflect.ClassTag

import io.duna.eventbus.EventBus
import io.duna.eventbus.event.Emitter
import io.duna.types.DefaultsTo

class DslEmitterBuilder {

  def event(name: String)(implicit eventBus: EventBus): Emitter = eventBus.emit(name)
}
