package io.duna.eventbus

import java.util.UUID

import scala.collection.mutable
import scala.reflect.ClassTag

import io.duna.eventbus.messaging.{Message, MessageDispatcher}

class DefaultEmitter[T: ClassTag](private val event: String,
                                  private val dispatcher: MessageDispatcher)
  extends Emitter[T] {

  private val headers = mutable.Map[String, String]()

  override def !(attachment: Option[T]): Unit = {
    val message = Message(
      source = Context().currentEvent,
      target = event,
      responseEvent = None,
      headers = headers.toMap,
      attachment = attachment
    )

    dispatcher dispatch message
  }

  override def ?[V: ClassTag](attachment: Option[T]): Subscriber[V] = {
    val responseEventName = s"responseFrom:$event:${UUID.randomUUID().toString}"
    val responseSubscriber: Subscriber[V] = Context().eventBus subscribeTo responseEventName

    val message = Message(
      source = Context().currentEvent,
      target = event,
      responseEvent = Some(responseEventName),
      headers = headers.toMap,
      attachment = attachment
    )

    dispatcher dispatch message

    responseSubscriber
  }

  override def header(pair: (String, String)): Emitter[T] = {
    headers += pair
    this
  }
}
