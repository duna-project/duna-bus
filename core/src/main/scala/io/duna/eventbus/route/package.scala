package io.duna.eventbus

package object route {

  implicit class RoutingList(private val self: List[Route[_]]) extends AnyVal {
    def select(event: String)(implicit strategy: RoutingStrategy): List[Route[_]] = {
      strategy.select(event, self) match {
        case Some(r) => List(r)
        case None => List.empty
      }
    }
  }
}
