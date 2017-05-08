package io.duna.eventbus.routing

import scala.collection.mutable
import scala.reflect.ClassTag

import io.duna.collection.concurrent.ConcurrentHashMultimap
import io.duna.eventbus.Listener
import io.duna.eventbus.message.Message

class Router {

  private val eventToListenersMapping = ConcurrentHashMultimap[String, Listener[_]]()

  def <->[T: ClassTag](event: String): Route[T] = route[T](event)

  def -/>(event: String): Boolean = removeAll(event)

  def -/>(route: Listener[_]): Boolean = remove(route)

  def route[T: ClassTag](event: String): Route[T] = new Route[T](event, this)

  def remove(listener: Listener[_]): Boolean =
    if (listener == null) false
    else eventToListenersMapping --= ((listener.event, listener))

  def removeAll(event: String): Boolean = eventToListenersMapping -= event

  def matching[T](message: Message[T]): Set[Listener[_]] = eventToListenersMapping
    .get(message.target)
    .getOrElse(mutable.Set.empty)
    .filter(_.matches(message))
    .toSet

  private[routing] def registerRoute(event: String, listener: Listener[_]): Unit = {
    eventToListenersMapping += ((listener.event, listener))
  }
}

class Route[T: ClassTag](val event: String,
                         val router: Router) {

  def to(listener: Listener[T]): Unit = {
    if (listener == null) throw new NullPointerException
    router.registerRoute(event, listener)
  }
}


