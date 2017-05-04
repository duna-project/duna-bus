package io.duna.eventbus

import java.util.concurrent.ConcurrentHashMap
import java.util.{Collections, UUID}
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.reflect.ClassTag

import io.duna.collection.concurrent.ConcurrentHashMultimap
import io.duna.eventbus.messaging.Message

class Router {

  private val routes = ConcurrentHashMultimap[String, Route[_]]()

  def <->[T](event: String): Route[T] = route(event)

  def /->(event: String): Boolean = removeAllRoutes(event)

  def /->(route: Route[_]): Boolean = removeRoute(route)

  def route[T](event: String): Route[T] = new Route[T](event)

  def removeRoute(route: Route[_]): Boolean = routes --= ((route.event, route))

  def removeAllRoutes(event: String): Boolean = routes -= event

  def dispatch[T](message: Message[T]): Unit = {

  }

  protected[eventbus] def registerRoute(route: Route[_]): Unit =
    routes += ((route.event, route))

}

case class Route[T: ClassTag](event: String,
                              routeId: String = UUID.randomUUID().toString) {

  def onError(): Route[_ <: Exception] = ???

  def when(matcher: Message[T] => Boolean): Route[T] = ???

  def when(error: dsl.isError.type): Route[_ <: Exception] = ???

  def to(handler: T => Unit): Route[T] = ???

}
