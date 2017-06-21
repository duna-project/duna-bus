package io.duna.eventbus.route

trait RoutingStrategy {

  def select(event: String, routes: List[Route[_]]): Option[Route[_]]
}
