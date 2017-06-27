package io.duna.eventbus.event

import java.util.UUID

import scala.collection.mutable
import scala.concurrent.Future
import scala.reflect.runtime.universe.{TypeTag, typeOf}

import io.duna.eventbus.{EventBus, message}
import io.duna.eventbus.message._
import io.duna.types.DefaultsTo

class DefaultEmitter(val event: String)
                    (implicit eventBus: EventBus, postman: Postman)
  extends Emitter {

  private val headers: mutable.Map[Symbol, String] = mutable.HashMap[Symbol, String]()

  override def request[A: TypeTag, R: TypeTag](attachment: Option[A] = None)
                                              (implicit default: A DefaultsTo Unit): Future[Option[R]] = {
    require(attachment != null, "The attachment cannot be null. Use None instead.")
    this withHeader '$_messageType -> "request"

    val replyEvent = s"duna-reply:${UUID randomUUID() toString()}"
    val future = ReplyListener[R](replyEvent).future

    postman deliver message.Request(event, Some(replyEvent), headers.toMap, attachment)

    future
  }

  override def send[A: TypeTag](attachment: Option[A] = None)
                               (implicit default: A DefaultsTo Unit): Unit = {
    require(attachment != null, "The attachment cannot be null. Use None instead.")
    this withHeader '$_messageType -> "unicast"

    typeOf[A] match {
      case t if !(t =:= typeOf[Nothing]) && t <:< typeOf[Throwable] =>
        require(attachment.isDefined, "The exception must be defined.")

        attachment match {
          case Some(a: Throwable) => postman deliver message.Error(event, None, headers.toMap, a, Unicast)
          case _ => throw new IllegalArgumentException
        }
      case _ =>
        postman deliver message.Event(event, headers.toMap, attachment, Unicast)
    }
  }

  override def broadcast[A: TypeTag](attachment: Option[A] = None)
                                    (implicit default: A DefaultsTo Unit): Unit = {
    require(attachment != null, "The attachment cannot be null. Use None instead.")
    this withHeader '$_messageType -> "broadcast"

    typeOf[A] match {
      case t if t <:< typeOf[Throwable] =>
        require(attachment.isDefined, "The exception must be defined.")

        attachment match {
          case Some(a: Throwable) => postman deliver message.Error(event, None, headers.toMap, a, Broadcast)
          case _ => throw new IllegalArgumentException
        }
      case _ =>
        postman deliver message.Event(event, headers.toMap, attachment, Broadcast)
    }
  }

  override def complete(): Unit = {
    this withHeader '$_messageType -> "completion"
    postman deliver Signal(event, headers.toMap, Unicast, Completion)
  }

  override def withHeader(key: Symbol, value: String): Emitter = {
    headers(key) = value
    this
  }

  override def withHeader(header: (Symbol, String)): Emitter = {
    headers += header
    this
  }
}

object DefaultEmitter {
  def apply(event: String)(implicit eventBus: EventBus, postman: Postman): Emitter =
    new DefaultEmitter(event)
}
