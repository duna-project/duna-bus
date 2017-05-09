package io.duna.eventbus

import java.util.UUID

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.util.Try

import io.duna.eventbus.message.{Message, Messenger}
import io.duna.eventbus.routing.Router

class DefaultEmitter[T: ClassTag](private val event: String,
                                  private val messenger: Messenger)
                                 (implicit sourceEvent: Option[String], router: Router)
  extends Emitter[T] {

  private val headers = mutable.Map[String, String]()

  override def dispatch(attachment: Option[T] = None): Unit =
    doDispatch(attachment, None)

  override def expectReply[V: ClassTag](): Emitter[T] with Listener[V] =
    new ReplyableEmitter[T, V](UUID.randomUUID().toString) {
      val emitter: Emitter[T] = DefaultEmitter.this
    }

  override def withHeader(pair: (String, String)): Emitter[T] = {
    headers += pair
    this
  }

  private[eventbus] def doDispatch(attachment: Option[T] = None,
                                  responseEvent: Option[String]): Unit = {
    val message = Message(
      source = sourceEvent,
      target = event,
      responseEvent = responseEvent,
      headers = headers.toMap,
      attachment = attachment
    )

    messenger send message
  }
}
