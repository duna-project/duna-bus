package io.duna.eventbus.route

import scala.collection.mutable

class RoundRobinRoutingStrategy extends RoutingStrategy {

  private val indexMap = mutable.HashMap[String, Int]()

  override def select(event: String, routes: List[Route[_]]): Option[Route[_]] = {
    if (routes.isEmpty) return None

    val index = indexMap.getOrElseUpdate(event, 0)
    indexMap.put(event, index + 1)

    Some(routes(index % routes.size))
  }

}
