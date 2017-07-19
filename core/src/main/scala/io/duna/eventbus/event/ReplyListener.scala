package io.duna.eventbus.event

import scala.reflect.runtime.universe.TypeTag
import scala.util.{Failure, Success, Try}

import io.duna.concurrent.future.{Future, Promise}
import io.duna.dsl._
import io.duna.eventbus.EventBus

class ReplyListener[T: TypeTag](event: String)
                               (implicit eventBus: EventBus)
  extends Listener[T] {

  listen only once to event

  private val promise = Promise[Option[T]]()

  override def onNext(value: Option[_ <: T]): Unit = promise.tryComplete(Success(value))

  override def onError(error: Throwable): Unit = promise.tryComplete(Failure(error))

  override def onComplete(): Unit = promise.tryComplete(Success(None))

  def future: Future[Option[T]] = promise
}

object ReplyListener {
  def apply[T: TypeTag](event: String)(implicit eventBus: EventBus): ReplyListener[T] =
    new ReplyListener[T](event)
}
