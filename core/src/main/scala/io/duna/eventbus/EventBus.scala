package io.duna.eventbus

import scala.reflect.runtime.universe.TypeTag

import com.twitter.util.Future
import io.duna.eventbus.event.{Emitter, Listener}
import io.duna.eventbus.message.Message
import io.duna.eventbus.route.Route
import io.duna.types.DefaultsTo
import io.netty.util.concurrent.EventExecutorGroup

trait EventBus {

  val nodeId: String

  val eventLoopGroup: EventExecutorGroup

  def route[T: TypeTag](event: String)(implicit default: T DefaultsTo Unit): Route[T]

  def unroute(event: String, listener: Listener[_]): Future[Listener[_]]

  def emit(event: String): Emitter

  def clear(event: String): List[Listener[_]]

  def consume(message: Message[_]): Unit

  def errorHandler: Throwable => Unit

  def errorHandler_=(handler: Throwable => Unit): Unit
}
