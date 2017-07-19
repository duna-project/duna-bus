package io.duna.eventbus

import scala.collection.immutable.HashMap
import scala.collection.mutable
import scala.ref.WeakReference

import io.duna.eventbus.message.Message
import io.netty.util.concurrent.EventExecutor

class Context(val owner: EventBus) extends mutable.HashMap[Symbol, Any] {

  private var _currentEvent: String = _
  private var _replyTo: Option[String] = _
  private var _headers: Map[Symbol, String] = _

  def currentEvent: String = _currentEvent

  def currentEvent_=(event: String): Unit = {
    require(event != null, "The event cannot be null.")
    _currentEvent = event
  }

  def replyEvent: Option[String] = _replyTo

  def replyEvent_=(event: Option[String]): Unit = {
    _replyTo =
      if (event == null) None
      else event
  }

  def headers: Map[Symbol, String] = _headers

  def headers_=(headers: Map[Symbol, String]): Unit = {
    require(headers != null, "The headers map cannot be null.")
    _headers = headers
  }

  def updateFrom(message: Message[_]): Context = {
    this.currentEvent = message.target
    this.replyEvent = message.responseEvent
    this.headers = message.headers

    this
  }

  def assign(): Unit = Context.current = this
}

object Context {

  private val contextHolder = new ThreadLocal[Context]

  def apply(): Context = contextHolder.get()

  def current: Context = apply()

  def current_=(value: Context): Unit = contextHolder.set(value)

  def createFrom(message: Message[_], owner: EventBus, eventLoop: EventExecutor): Context = {
    val context = new Context(owner)
    context.currentEvent = message.target
    context.replyEvent = message.responseEvent
    context.headers = message.headers

    context
  }
}