package io.duna.eventbus

import java.util.concurrent.atomic.AtomicReference

import io.duna.eventbus.dsl.isError
import io.duna.eventbus.routing.{Route, Router}

protected case class DefaultSubscriber[T](override val event: String,
                                          private val router: Router)
  extends Subscriber[T] {

  val active = true

  private val route = new AtomicReference[Route[T]]()
  private val errorRoute = new AtomicReference[Route[Exception]]()

  override def onReceive(handler: Option[T] => Unit): Subscriber[T] = {
    val oldRoute = route.get()
    val newRoute = router <-> event to handler

    if (route.compareAndSet(oldRoute, newRoute)) {
      router.removeRoute(oldRoute)
    } else {
      router.removeRoute(newRoute)
    }

    this
  }

  override def onError(errorHandler: Option[Exception] => Unit): Subscriber[T] = {
    val oldRoute = errorRoute.get()
    val newRoute = router <-> event when isError to errorHandler

    if (errorRoute.compareAndSet(oldRoute, newRoute)) {
      router.removeRoute(oldRoute)
    } else {
      router.removeRoute(newRoute)
    }

    this
  }

  override def unsubscribe(): Unit = this.synchronized {
    router /-> route.get()
    router /-> errorRoute.get()
  }

  override def next(arg: Option[T]): Unit = ???

  override def nextError(arg: _ <: Exception): Unit = ???
}
