package io.duna.eventbus.routing

import scala.reflect.ClassTag

import io.duna.collection.concurrent.ConcurrentHashMultimap
import io.duna.eventbus.messaging.Message

class Router {

  private val eventToRoutesMapping = ConcurrentHashMultimap[String, Route[_]]()

  def <->[T](event: String): Route[T] = route(event)

  def /->(event: String): Boolean = removeAllRoutes(event)

  def /->(route: Route[_]): Boolean = removeRoute(route)

  def route[T: ClassTag](event: String): Route[T] = new Route[T](event, this)

  def removeRoute(route: Route[_]): Boolean = eventToRoutesMapping --= ((route.event, route))

  def removeAllRoutes(event: String): Boolean = eventToRoutesMapping -= event

  def matching[T](message: Message[T]) = new Any {
    def dispatch(func: (Route[T]) => Unit): Unit = {
      eventToRoutesMapping
        .get(message.target)
        .getOrElse(Set.empty)
        .filter(_.matches(message))
        .toSet[Route[T]]
        .foreach(func)
    }
  }

  private[routing] def registerRoute(route: Route[_]): Unit = {
    if (!route.registered) throw new IllegalArgumentException

    eventToRoutesMapping += ((route.event, route))
  }

}

