package io.duna.cluster

import io.duna.eventbus.EventBus

trait NodeRegistry {

  def register(event: String, eventBus: EventBus): Unit

  def unregister(event: String, eventBus: EventBus): Unit

}
