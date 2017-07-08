package io.duna.eventbus

import scala.concurrent.Future
import scala.reflect.runtime.universe.TypeTag

import io.duna.eventbus.event.{Emitter, Listener}
import io.duna.eventbus.message.Message
import io.duna.eventbus.routing.Route
import io.duna.types.DefaultsTo
import io.netty.util.concurrent.EventExecutorGroup

trait EventBus {

  val nodeId: String

  val eventLoopGroup: EventExecutorGroup

  def route[A: TypeTag](event: String)(implicit default: A DefaultsTo Unit): Route[A]

  def unroute(event: String, listener: Listener[_, _]): Future[Listener[_, _]]

  def emit(event: String): Emitter

  def clear(event: String): Set[Listener[_, _]]

  def consume(message: Message[_]): Unit

  def errorHandler: Throwable => Unit

  def errorHandler_=(handler: Throwable => Unit): Unit

  private[duna] def tryUnroute(event: String, listener: Listener[_, _]): Boolean
}
