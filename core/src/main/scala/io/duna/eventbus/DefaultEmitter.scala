package io.duna.eventbus

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag
import scala.util.Try

import io.duna.eventbus.dsl.reply
import io.duna.eventbus.message.{Message, Postman}
import io.duna.eventbus.routing.Router

class DefaultEmitter[T: ClassTag](private val event: String,
                                  private val sourceEvent: Option[String],
                                  private val messenger: Postman)
                                 (implicit router: Router,
                                  executionContext: ExecutionContext)
  extends Emitter[T] {

  private val headers = mutable.Map[String, String]()

  override def dispatch(attachment: Option[T] = None): Emitter[T] = {
    doDispatch(attachment, None)
    this
  }

  override def expect[V: ClassTag](r: reply.type): ReplyableEmitter[T, V] =
    new ReplyableEmitter[T, V](UUID.randomUUID().toString) {
      val emitter: Emitter[T] = DefaultEmitter.this
    }

  override def withHeader(pair: (String, String)): Emitter[T] = {
    headers += pair
    this
  }

  private[eventbus] def doDispatch(attachment: Option[T] = None,
                                   responseEvent: Option[String]): Unit = {
    val message = Message[T](
      source = sourceEvent,
      target = event,
      responseEvent = responseEvent,
      headers = headers.toMap,
      attachment = attachment
    )

    messenger deliver message
  }
}
