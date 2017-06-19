package io.duna.eventbus.route

import java.util.concurrent.atomic.AtomicBoolean

import scala.reflect.runtime.universe._

import io.duna.eventbus.dsl
import io.duna.eventbus.dsl.once
import io.duna.eventbus.event.Listener
import io.duna.eventbus.message.{Signal, Error, Message}

final class Route[T](val event: String,
                               private val router: Router) {

  private var filter: PartialFunction[Message[T], Boolean] = {
    case _: Message[T] => true
  }

  private[eventbus] var listener: Listener[T] = _

  private var _listenOnlyOnce: Boolean = false
  private val _complete = new AtomicBoolean(false)

  def listenOnce: Boolean = _listenOnlyOnce

  def isComplete: Boolean = _complete.get()

  def complete(): Boolean = _complete.compareAndSet(false, true)

  def accept(message: Message[_]): Boolean = {
    message.typeTag.tpe match {
      case SingleType(typ, _) => false
      case TypeRef(_, _, List(typ)) if typ =:= typeOf[Nothing] => true
    }

//    message match {
//      case _: Error[_] | _: Signal => true
//      case m if m.attachmentType == classOf[Nothing] =>
//        this.filter(message.asInstanceOf[Message[T]])
//      case m if listener.messageType.isAssignableFrom(m.attachmentType) =>
//        this.filter(message.asInstanceOf[Message[T]])
//      case _ => false
//    }
  }

  def when(filter: PartialFunction[Message[T], Boolean]): Route[T] = {
    this.filter = filter
    this
  }

  def only(once: dsl.once.type): Route[T] = {
    _listenOnlyOnce = true
    this
  }

  def to(listener: Listener[T]): Unit = {
    require(listener != null, "The listener cannot be null")
    this.listener = listener

    router.register(this)
  }


  override def equals(other: Any): Boolean = other match {
    case that: Route[_] =>
      listener == that.listener &&
        _listenOnlyOnce == that._listenOnlyOnce &&
        event == that.event
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(listener, _listenOnlyOnce, event)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
