package io.duna.eventbus.dsl

import com.twitter.util.Future
import io.duna.eventbus.EventBus
import io.duna.eventbus.event.{DslListener, Listener}

class DslListenerRemoval(implicit eventBus: EventBus) {

  var listenerRef: DslListener[_] = _

  def listener(ref: DslListener[_]): DslListenerRemoval = {
    listenerRef = ref
    this
  }

  def from(event: String): Future[Listener[_]] = {
    eventBus unroute (event, listenerRef.listener)
  }
}
