package io.duna.eventbus.routing.strategy

import scala.collection.immutable.SortedSet
import scala.collection.mutable

import io.duna.eventbus.routing.Route

class RoundRobinRoutingStrategy extends RoutingStrategy {

  private val indexMap = mutable.HashMap[String, Int]()

  override def select(event: String, routes: SortedSet[Route[_]]): Option[Route[_]] = {
    if (routes.isEmpty) return None

    val index = indexMap.getOrElseUpdate(event, 0)
    indexMap.put(event, index + 1)

    Some(routes.iterator.drop(index % routes.size).next())
  }

}
