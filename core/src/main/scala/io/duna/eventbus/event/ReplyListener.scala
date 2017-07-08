package io.duna.eventbus.event

import scala.concurrent.{Future, Promise}
import scala.reflect.runtime.universe.TypeTag

import io.duna.eventbus.EventBus

class ReplyListener[T: TypeTag](event: String)
                                (implicit eventBus: EventBus)
  extends Listener[T] {

  listen only once to event

  private val promise = Promise[Option[T]]()

  override def onNext(value: Option[_ <: T]): Unit = promise.trySuccess(value)

  override def onError(error: Throwable): Unit = promise.tryFailure(error)

  override def onComplete(): Unit = promise.trySuccess(None)

  def future: Future[Option[T]] = promise.future
}

object ReplyListener {
  def apply[T: TypeTag](event: String)(implicit eventBus: EventBus): ReplyListener[T] =
    new ReplyListener[T](event)
}
