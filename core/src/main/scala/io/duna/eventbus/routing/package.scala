package io.duna.eventbus

import scala.collection.immutable.SortedSet

import io.duna.eventbus.routing.strategy.RoutingStrategy

package object routing {

  implicit class RoutingSortedSet(private val self: SortedSet[Route[_]]) extends AnyVal {
    def select(event: String)(implicit strategy: RoutingStrategy,
                              ordering: Ordering[Route[_]]): SortedSet[Route[_]] = {
      strategy.select(event, self) match {
        case Some(r) => SortedSet(r)
        case None => SortedSet.empty[Route[_]]
      }
    }
  }
}
