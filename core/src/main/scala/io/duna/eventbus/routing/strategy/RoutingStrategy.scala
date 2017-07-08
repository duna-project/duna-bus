package io.duna.eventbus.routing.strategy

import scala.collection.immutable.SortedSet

import io.duna.eventbus.routing.Route

trait RoutingStrategy {

  def select(event: String, routes: SortedSet[Route[_]]): Option[Route[_]]
}
