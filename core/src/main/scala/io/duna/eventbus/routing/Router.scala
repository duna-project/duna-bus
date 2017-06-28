package io.duna.eventbus.routing

import java.util.concurrent.ConcurrentHashMap

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.collection.concurrent
import scala.collection.immutable.SortedSet
import scala.collection.parallel.mutable.ParHashMap
import scala.concurrent.{Future, Promise}
import scala.reflect.runtime.universe.TypeTag
import scala.util.Try

import io.duna.collection.NonBlockingHashMap
import io.duna.eventbus.event.Listener
import io.duna.eventbus.message.{Broadcast, Message}
import io.duna.eventbus.routing.strategy.RoundRobinRoutingStrategy
import io.netty.util.concurrent.EventExecutorGroup

class Router(private val eventLoopGroup: EventExecutorGroup) {

  protected[this] val routes: concurrent.Map[String, SortedSet[Route[_]]] =
    new NonBlockingHashMap[String, SortedSet[Route[_]]]().asScala

  private implicit val routingStrategy = new RoundRobinRoutingStrategy

  private implicit val ordering = Ordering.by[Route[_], String](r => r.listener.listenerId)

  def hasRouteTo(event: String): Boolean = routes.contains(event)

  def route[A: TypeTag](event: String): Route[A] = new Route[A](event, this)

  def unroute(event: String, listener: Listener[_]): Future[Listener[_]] = {
    val promise = Promise[Listener[_]]()

    eventLoopGroup.next() execute { () =>
      deregister(event, listener)
      promise.complete(Try(listener))
    }

    promise.future
  }

  def clear(event: String): Set[Listener[_]] =
    routes
      .remove(event)
      .getOrElse(SortedSet.empty)
      .map(_.listener)

  def routesFor(message: Message[_]): SortedSet[Route[_]] = {
    message match {
      case m if m.transmissionMode == Broadcast =>
        routes
          .getOrElse(message.target, SortedSet.empty)
          .filter(_.accept(message))
      case _ =>
        routes
          .getOrElse(message.target, SortedSet.empty)
          .filter(_.accept(message))
          .select(message.target)
    }
  }

  private[duna] final def tryRegister(route: Route[_]): Boolean = {
    val prevRoutes = routes.getOrElseUpdate(route.event, SortedSet.empty[Route[_]])
    val newRoutes = prevRoutes ++ List(route)

    routes.replace(route.event, prevRoutes, newRoutes)
  }

  private[duna] final def tryDeregister(event: String, listener: Listener[_]): Boolean = {
    val prevRoutes = routes.getOrElse(event, SortedSet.empty[Route[_]])
    val newRoutes = prevRoutes.filter(_.listener != listener)

    if (prevRoutes.isEmpty)
      return true

    if (newRoutes.isEmpty) {
      routes.remove(event, prevRoutes)
    } else {
      routes.replace(event, prevRoutes, newRoutes)
    }
  }

  @tailrec
  private[routing] final def register(route: Route[_], attempt: Int = 0): Unit = {
    if (attempt == 10000) return

    val prevRoutes = routes.getOrElseUpdate(route.event, SortedSet.empty[Route[_]])
    val newRoutes = prevRoutes ++ List(route)

    if (!routes.replace(route.event, prevRoutes, newRoutes))
      register(route, attempt + 1)
  }

  @tailrec
  private[routing] final def deregister(event: String, listener: Listener[_], attempt: Int = 0): Unit = {
    if (attempt == 10000) return

    val prevRoutes = routes.getOrElse(event, SortedSet.empty[Route[_]])
    val newRoutes = prevRoutes.filter(_.listener != listener)

    if (prevRoutes.isEmpty)
      return

    if (newRoutes.isEmpty) {
      if (!routes.remove(event, prevRoutes)) deregister(event, listener, attempt + 1)
    } else {
      if (!routes.replace(event, prevRoutes, newRoutes)) deregister(event, listener, attempt + 1)
    }
  }
}
