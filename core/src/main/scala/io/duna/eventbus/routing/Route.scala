package io.duna.eventbus.routing

import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import scala.reflect.ClassTag

import io.duna.eventbus.dsl
import io.duna.eventbus.messaging.Message

class Route[T: ClassTag](val event: String,
                         val router: Router,
                         val routeId: String = UUID.randomUUID().toString,
                         val registered: Boolean = false,
                         val handler: Option[T] => Unit = _,
                         matcher: Message[T] => Boolean = _) {

  val messageType: Class[T] = classOf[T]

  private[eventbus] val executing = new AtomicBoolean(false)

  def onError[E <: Exception : ClassTag](): Route[E] = when[E](dsl.isError)

  def when(newMatcher: Message[T] => Boolean): Route[T] = {
    if (newMatcher == null) throw new NullPointerException

    Route[T](event, router, routeId, registered = false, handler, newMatcher)
  }


  def when[E <: Exception : ClassTag](error: dsl.isError.type): Route[E] =
    new Route[E](event, router, routeId)(false, handler, matcher)

  def to(newHandler: Option[T] => Unit): Route[T] = {
    if (newHandler == null) throw new NullPointerException
    if (registered) throw new IllegalStateException

    val route = Route[T](event, router, routeId, registered = true, newHandler, matcher)
    router.registerRoute(route)
    route
  }

  def matches(message: Message[_]): Boolean = message.isInstanceOf[Message[T]] && matcher(message)

  def apply(message: Message[T]): Boolean = {
    if (!message.isInstanceOf[Message[T]]) return false

    if (executing.compareAndSet(false, true)) {
      handler(message.attachment)
      executing.set(false)
      true
    } else {
      false
    }
  }

  override def equals(other: Any): Boolean = other match {
    case that: Route[T] =>
      (that canEqual this) &&
        messageType == that.messageType &&
        event == that.event &&
        routeId == that.routeId
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(messageType, event, routeId)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  private def canEqual(other: Any): Boolean = other.isInstanceOf[Route]
}

object Route {
  def apply[T](event: String,
            router: Router,
            routeId: String = UUID.randomUUID().toString,
            registered: Boolean = false,
            handler: Option[T] => Unit = _,
            matcher: Message[T] => Boolean = _): Route[T] =
    new Route(event, router, routeId, registered, handler, matcher)
}
