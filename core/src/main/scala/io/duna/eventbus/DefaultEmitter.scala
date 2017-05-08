package io.duna.eventbus

import java.util.UUID

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.util.Try

import io.duna.eventbus.message.{Message, Messenger}
import io.duna.eventbus.routing.Router

class DefaultEmitter[T: ClassTag](private val event: String,
                                  private val messenger: Messenger)
                                 (implicit context: Context)
  extends Emitter[T] {

  private val headers = mutable.Map[String, String]()

  override def dispatch(attachment: Option[T] = None): Unit =
    doDispatch(attachment, None)

  override def expectReply[V: ClassTag](): Emitter[T] with Listener[V] =
    new DefaultListener[V](UUID.randomUUID().toString)
      with ReplyableEmitter[T, V] {
      val emitter: Emitter[T] = DefaultEmitter.this
      val router: Router = implicitly[Router]
    }

  override def withHeader(pair: (String, String)): Emitter[T] = {
    headers += pair
    this
  }

  private[Emitter] def doDispatch(attachment: Option[T] = None,
                                  responseEvent: Option[String]): Unit = {
    val message = Message(
      source = Try(context.currentEvent).toOption,
      target = event,
      responseEvent = responseEvent,
      headers = headers.toMap,
      attachment = attachment
    )

    messenger send message
  }
}
