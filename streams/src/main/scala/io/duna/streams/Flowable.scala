package io.duna.streams

import io.duna.streams.flowable.WrapAsScala._
import io.reactivex.functions
import io.{reactivex => rx}
import org.reactivestreams.{Publisher, Subscriber}

trait Flowable[A] extends Publisher[A] {
  private[streams] val javaFlowable: rx.Flowable[A]

  def flatMap[R](mapper: (_ >: A) => _ <: Publisher[_ <: R],
                 delayErrors: Boolean, maxConcurrency: Int, bufferSize: Int): Flowable[R] =
    javaFlowable.flatMap[R](new functions.Function[A, Publisher[_ <: R]] {
      override def apply(t: A): Publisher[_ <: R] = mapper(t)
    }, delayErrors, maxConcurrency, bufferSize)

  override def subscribe(s: Subscriber[_ >: A]): Unit = javaFlowable.subscribe(s)
}

object Flowable {

  def apply[A](items: Array[A]): Flowable[A] = Flowable(rx.Flowable.fromArray(items: _*))

  private def apply[A](underlying: rx.Flowable[A]) = new Flowable[A] {
    override private[streams] val javaFlowable = underlying
  }
}
