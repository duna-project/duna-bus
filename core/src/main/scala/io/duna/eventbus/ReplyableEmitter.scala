package io.duna.eventbus

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

import io.duna.eventbus.dsl.reply
import io.duna.eventbus.routing.Router

abstract class ReplyableEmitter[T: ClassTag, R: ClassTag](event: String)
                                                         (implicit router: Router,
                                                          executionContext: ExecutionContext)
  extends DefaultListener[R](event) with Emitter[T] {

  val emitter: Emitter[T]

  override def expect[V: ClassTag](r: reply.type): ReplyableEmitter[T, V] = {
    val replyClass = implicitly[ClassTag[V]].runtimeClass
    val listenerClass = implicitly[ClassTag[R]].runtimeClass

    if (replyClass.isAssignableFrom(listenerClass)) this.asInstanceOf[ReplyableEmitter[T, V]]
    else emitter expect reply
  }

  override def dispatch(attachment: Option[T]): Unit = {
    router route[R] event onceTo this
    emitter.asInstanceOf[DefaultEmitter[T]].doDispatch(attachment, Some(event))
  }

  override def withHeader(header: (String, String)): Emitter[T] = emitter.withHeader(header)

  override def onReceive(handler: (Option[R]) => Unit): ReplyableEmitter[T, R] =
    super.onReceive(handler).asInstanceOf[ReplyableEmitter[T, R]]

  override def onError(handler: (Throwable) => Unit): ReplyableEmitter[T, R] =
    super.onError(handler).asInstanceOf[ReplyableEmitter[T, R]]

  override def when(matcher: (Option[_], Context) => Boolean): ReplyableEmitter[T, R] =
    super.when(matcher).asInstanceOf[ReplyableEmitter[T, R]]
}
