package io.duna.eventbus

import scala.reflect.ClassTag

import io.duna.eventbus.message.Message
import io.duna.eventbus.routing.Router

trait ReplyableEmitter[T: ClassTag, R: ClassTag]
  extends Emitter[T] with Listener[R] {

  val emitter: Emitter[T]
  val router: Router

  override def expectReply[V: ClassTag](): Emitter[T] with Listener[V] =
    if (classOf[V].isAssignableFrom(classOf[R])) this.asInstanceOf[Emitter[T] with Listener[V]]
    else emitter.expectReply()

  override def dispatch(attachment: Option[T]): Unit = {
    router route event to this
    emitter.asInstanceOf[DefaultEmitter].doDispatch(attachment, Some(event))
  }

  override def withHeader(header: (String, String)): Emitter[T] = emitter.withHeader(header)

  override private[eventbus] def nextValue(value: Option[_]) = listener.nextValue(value)

  override private[eventbus] def nextError(error: Throwable) = listener.nextError(error)

  override private[eventbus] def matches(message: Message[_]) = listener.matches(message)
}
