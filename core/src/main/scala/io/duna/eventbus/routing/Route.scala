package io.duna.eventbus.routing

import java.util.concurrent.atomic.AtomicBoolean

import scala.reflect.runtime.universe._

import io.duna.dsl
import io.duna.dsl.once
import io.duna.eventbus.event.Listener
import io.duna.eventbus.message.{Signal, Error, Message}

final class Route[A](val event: String,
                     private val router: Router) {

  private var filter: PartialFunction[Message[A], Boolean] = {
    case _: Message[A] => true
  }

  private[eventbus] var listener: Listener[A] = _

  private var _listenOnlyOnce: Boolean = false
  private val _complete = new AtomicBoolean(false)

  def listenOnce: Boolean = _listenOnlyOnce

  def isComplete: Boolean = _complete.get()

  def complete(): Boolean = _complete.compareAndSet(false, true)

  def accept(message: Message[_]): Boolean = {
    message match {
      case _: Error[_] | _: Signal => true
      case m if m.typeTag.tpe =:= typeOf[Nothing] =>
        this.filter(message.asInstanceOf[Message[A]])
      case m if m.typeTag.tpe <:< listener.messageType.tpe =>
        this.filter(message.asInstanceOf[Message[A]])
      case _ => false
    }
  }

  def when(filter: PartialFunction[Message[A], Boolean]): Route[A] = {
    this.filter = filter
    this
  }

  def only(once: dsl.once.type): Route[A] = {
    _listenOnlyOnce = true
    this
  }

  def to(listener: Listener[A]): Unit = {
    require(listener != null, "The listener cannot be null")
    this.listener = listener

    router.register(this)
  }

  private[duna] def tryTo(listener: Listener[A]): Boolean = {
    require(listener != null, "The listener cannot be null")
    this.listener = listener

    router.tryRegister(this)
  }

  override def equals(other: Any): Boolean = other match {
    case that: Route[_] =>
      listener == that.listener &&
        event == that.event
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(listener, event)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
