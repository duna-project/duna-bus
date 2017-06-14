package io.duna.eventbus.event

import java.util.UUID

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.util.Try

import com.twitter.util.Future
import io.duna.eventbus.{Context, EventBus}
import io.duna.eventbus.message
import io.duna.eventbus.message.{Broadcast, Completion, Postman, Unicast}
import io.duna.types.DefaultsTo

class DefaultEmitter(val event: String)
                    (implicit eventBus: EventBus, postman: Postman)
  extends Emitter {

  private val headers: mutable.Map[Symbol, String] = mutable.HashMap[Symbol, String]()

  override def request[A: ClassTag, R: ClassTag](attachment: Option[A] = None)
                                                (implicit default: A DefaultsTo Unit): Future[Option[R]] = {
    require(attachment != null, "The attachment cannot be null. Use None instead.")
    this withHeader '$_messageType -> "request"

    val replyEvent = s"duna-reply:${UUID randomUUID() toString()}"
    val future = ReplyListener[R](replyEvent).future

    postman deliver new message.Request(event, Some(replyEvent), headers.toMap, attachment)

    future
  }

  override def send[A: ClassTag](attachment: Option[A] = None)
                                (implicit default: A DefaultsTo Unit): Unit = {
    require(attachment != null, "The attachment cannot be null. Use None instead.")
    this withHeader '$_messageType -> "unicast"

    postman deliver new message.Event(event, headers.toMap, attachment, Unicast)
  }

  override def broadcast[A: ClassTag](attachment: Option[A] = None)
                                     (implicit default: A DefaultsTo Unit): Unit = {
    require(attachment != null, "The attachment cannot be null. Use None instead.")
    this withHeader '$_messageType -> "broadcast"

    postman deliver new message.Event(event, headers.toMap, attachment, Broadcast)
  }

  override def complete(): Unit = {
    this withHeader '$_messageType -> "completion"
    postman deliver Completion(event, headers.toMap, Unicast)
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
