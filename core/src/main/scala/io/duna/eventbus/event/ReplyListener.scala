package io.duna.eventbus.event

import scala.reflect.ClassTag
import scala.util.Try

import com.twitter.util.{Future, Promise}
import io.duna.eventbus.EventBus

class ReplyListener[T: ClassTag](event: String)
                                (implicit eventBus: EventBus)
  extends Listener[T] {

  listen only once to event

  private val promise = Promise[Option[T]]()

  override def onNext(value: Option[T]): Unit = Try(promise.setValue(value))

  override def onError(error: Throwable): Unit = Try(promise.setException(error))

  override def onComplete(): Unit = Try(promise.setValue(None))

  def future: Future[Option[T]] = promise
}

object ReplyListener {
  def apply[T: ClassTag](event: String)(implicit eventBus: EventBus): ReplyListener[T] =
    new ReplyListener[T](event)
}
