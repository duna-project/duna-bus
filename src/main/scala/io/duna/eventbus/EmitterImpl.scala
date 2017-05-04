package io.duna.eventbus

import java.util.UUID

import scala.collection.mutable
import scala.reflect.ClassTag

import io.duna.eventbus.messaging.{Message, MessageDispatcher}

class EmitterImpl[T: ClassTag](private val event: String,
                               private val dispatcher: MessageDispatcher[T])
  extends Emitter[T] {

  private val headers = mutable.Map[String, String]()

  override def !(attachment: Option[T]): Unit = {
    val message = Message(
      source = None,
      target = event,
      responseEvent = None,
      headers = headers.toMap,
      attachment = attachment
    )

    dispatcher dispatch message
  }

  override def ?[V: ClassTag](attachment: Option[T]): Subscriber[V] = {
    val responseEventName = UUID.randomUUID().toString
    val responseSubscriber: Subscriber[V] = EventBus() subscribeTo[V] responseEventName

    val message = Message(
      source = None,
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
