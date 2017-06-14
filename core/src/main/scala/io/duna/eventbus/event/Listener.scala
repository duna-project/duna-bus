package io.duna.eventbus.event

import java.util.UUID

import scala.reflect.ClassTag

import io.duna.eventbus.dsl.ListenerBuilder
import io.duna.eventbus.{Context, EventBus, dsl}

/** Represents an event listener.
  *
  * @tparam A the type of values processed by this listener.
  */
abstract class Listener[A: ClassTag](implicit eventBus: EventBus) {

  /** The type of message accepted by this listener */
  val messageType: Class[A] = implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]]

  /** Process the next event value emitted by the [EventBus].
    *
    * @param value the value emitted.
    */
  def onNext(value: Option[A] = None): Unit = {}

  /** Process an event error emitted by the [EventBus].
    *
    * @param error the error emitted.
    */
  def onError(error: Throwable): Unit = {}

  /** Finishes the processing of the event emitted by the [EventBus]. */
  def onComplete(): Unit = {}

  def context: Context = Context.current

  /** Binds this listener to an event. */
  @inline final def listen = new ListenerBuilder[A](this)

  /** DSL to inform that this should listen only once */
  @inline final def once: dsl.once.type = dsl.once

  private[eventbus] val listenerId: String = UUID.randomUUID().toString

  private[eventbus] val executor = eventBus.eventLoopGroup.next()

}
