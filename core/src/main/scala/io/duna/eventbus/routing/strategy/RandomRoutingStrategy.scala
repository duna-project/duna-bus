package io.duna.eventbus.routing.strategy

import scala.collection.immutable.SortedSet
import scala.util.Random

import io.duna.eventbus.routing.Route

class RandomRoutingStrategy extends RoutingStrategy {

  override def select(event: String, routes: SortedSet[Route[_]]): Option[Route[_]] = {
    if (routes.isEmpty) None
    else Some(routes.iterator.drop(Random.nextInt(routes.size)).next())
  }

}
