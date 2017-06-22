package io.duna.eventbus.route

import java.util.concurrent.ConcurrentHashMap

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.collection.concurrent
import scala.reflect.runtime.universe.TypeTag

import com.twitter.util.{Future, Promise}
import io.duna.eventbus.event.Listener
import io.duna.eventbus.message.{Broadcast, Message, Request}
import io.netty.util.concurrent.EventExecutorGroup

class Router(private val eventLoopGroup: EventExecutorGroup) {

  protected[this] val routes: concurrent.Map[String, List[Route[_]]] =
    new ConcurrentHashMap[String, List[Route[_]]]().asScala

  private implicit val routingStrategy = new RoundRobinRoutingStrategy

  def hasRouteTo(event: String): Boolean = routes.contains(event)

  def route[A: TypeTag](event: String): Route[A] = new Route[A](event, this)

  def unroute(event: String, listener: Listener[_]): Future[Listener[_]] = {
    val promise = Promise[Listener[_]]()

    eventLoopGroup.next() execute { () =>
      deregister(event, listener)
      promise.setValue(listener)
    }

    promise
  }

  def clear(event: String): List[Listener[_]] =
    routes
      .remove(event)
      .getOrElse(List.empty)
      .map(_.listener)

  def routesFor(message: Message[_]): List[Route[_]] = {
    message match {
      case m if m.transmissionMode == Broadcast =>
        routes
          .getOrElse(message.target, List.empty)
          .filter(_.accept(message))
      case _ =>
        routes
          .getOrElse(message.target, List.empty)
          .filter(_.accept(message))
          .select(message.target)
    }
  }

  @tailrec
  private[route] final def register(route: Route[_]): Unit = {
    val prevRoutes = routes.getOrElseUpdate(route.event, List.empty[Route[_]])
    val newRoutes = prevRoutes ++ List(route)

    if (!routes.replace(route.event, prevRoutes, newRoutes))
      register(route)
  }

  @tailrec
  private def deregister(event: String, listener: Listener[_]): Unit = {
    val prevRoutes = routes.getOrElse(event, List.empty[Route[_]])
    val newRoutes = prevRoutes.filter(_.listener != listener)

    if (prevRoutes.isEmpty)
      return

    if (newRoutes.isEmpty) {
      if (!routes.remove(event, prevRoutes)) deregister(event, listener)
    } else {
      if (!routes.replace(event, prevRoutes, newRoutes)) deregister(event, listener)
    }
  }
}
