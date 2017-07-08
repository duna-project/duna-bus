package io.duna.eventbus

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.reflect.runtime.universe.TypeTag
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

import io.duna.dsl
import io.duna.eventbus.errors.NoRouteFoundException
import io.duna.eventbus.event.{DefaultEmitter, Emitter, Listener}
import io.duna.eventbus.message.{Completion, Error, Message, Postman}
import io.duna.eventbus.routing.{Route, Router}
import io.duna.types.DefaultsTo
import io.netty.util.concurrent.EventExecutorGroup

class SingleNodeEventBus(override val eventLoopGroup: EventExecutorGroup) extends EventBus {

  val nodeId: String = UUID.randomUUID().toString

  private var _errorHandler: Throwable => Unit = { _ => }

  protected[this] val postman = new Postman {
    override def deliver(message: Message[_]): Unit = consume(message)
  }

  protected[this] val router = new Router(eventLoopGroup)

  private val contexts = new ConcurrentHashMap[Listener[_, _], Context].asScala

  def emit(event: String): Emitter = DefaultEmitter(event)(this, postman)

  override def route[T: TypeTag](event: String)
                                 (implicit default: T DefaultsTo Unit): Route[T] =
    router route[T] event

  override def unroute(event: String, listener: Listener[_, _]): Future[Listener[_]] =
    router unroute (event, listener)

  override private[duna] def tryUnroute(event: String, listener: Listener[_, _]) =
    router tryDeregister (event, listener)

  override def clear(event: String): Set[Listener[_, _]] =
    router clear event

  override def consume(message: Message[_]): Unit = {
    val matchedRoutes = router.routesFor(message)

    if (matchedRoutes.isEmpty) {
      _errorHandler(NoRouteFoundException(s"No routes found for event ${message.target}."))
      return
    }

    matchedRoutes.foreach { r =>
      val listener = r.listener

      if (r.listenOnce && !r.complete())
        unroute(r.event, r.listener)

      listener.executor execute { () =>
        val context = contexts.getOrElseUpdate(listener,
          Context.createFrom(message, this, eventLoopGroup.next()))

        context.updateFrom(message)
        context.assign()

        val result = message match {
          case Error(err) => Try(listener.onError(err))
          case Completion() => Try(listener.onComplete())
          case _ =>
            Try {
              listener.asInstanceOf[Listener[Any, Any]].onNext(message.attachment)
            }
        }

        result match {
          case Success(f: Future[_]) =>
            f onComplete {
              case Success(v) if context.replyTo.isDefined => dsl.reply(v)
              case Failure(e) if context.replyTo.isDefined => dsl.reply(e)

              case Failure(e) => errorHandler(e)
              case _ =>
            }

          case Success(v) if context.replyTo.isDefined => dsl.reply(v)
          case Failure(e) if context.replyTo.isDefined => dsl.reply(e)

          case Failure(e) => errorHandler(e)
          case _ =>
        }
      }
    }
  }

  override def errorHandler: Throwable => Unit = this._errorHandler

  override def errorHandler_=(handler: Throwable => Unit): Unit =
    _errorHandler = handler
}

object SingleNodeEventBus {
  def apply(eventExecutorGroup: EventExecutorGroup): SingleNodeEventBus = new SingleNodeEventBus(eventExecutorGroup)
}
