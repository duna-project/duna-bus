package io.duna.eventbus.route

import scala.util.Random

class RandomRoutingStrategy extends RoutingStrategy {

  override def select(event: String, routes: List[Route[_]]): Option[Route[_]] = {
    if (routes.isEmpty) None
    else Some(routes(Random.nextInt(routes.size)))
  }

}
