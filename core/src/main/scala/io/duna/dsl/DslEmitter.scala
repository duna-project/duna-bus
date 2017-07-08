package io.duna.dsl

import io.duna.eventbus.EventBus
import io.duna.eventbus.event.Emitter

class DslEmitter {

  def event(name: String)(implicit eventBus: EventBus): Emitter = eventBus emit name
}
