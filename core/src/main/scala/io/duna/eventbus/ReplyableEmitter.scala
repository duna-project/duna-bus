package io.duna.eventbus

import scala.reflect.ClassTag

import io.duna.eventbus.routing.Router

abstract class ReplyableEmitter[T: ClassTag, R: ClassTag](event: String)
                                                         (implicit router: Router)
  extends DefaultListener[R](event) with Emitter[T] {

  val emitter: Emitter[T]

  override def expectReply[V: ClassTag](): Emitter[T] with Listener[V] = {
    val replyClass = implicitly[ClassTag[V]].runtimeClass
    val listenerClass = implicitly[ClassTag[R]].runtimeClass

    if (replyClass.isAssignableFrom(listenerClass)) this.asInstanceOf[Emitter[T] with Listener[V]]
    else emitter.expectReply()
  }

  override def dispatch(attachment: Option[T]): Unit = {
    router route[R] event to this
    emitter.asInstanceOf[DefaultEmitter[T]].doDispatch(attachment, Some(event))
  }

  override def withHeader(header: (String, String)): Emitter[T] = emitter.withHeader(header)

}
