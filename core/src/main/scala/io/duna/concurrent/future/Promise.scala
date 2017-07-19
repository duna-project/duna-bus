package io.duna.concurrent.future

import java.util.concurrent.atomic.AtomicReference

import scala.util.Try

class Promise[A] extends Future[A] {

  private val valueRef = new AtomicReference[Option[Try[A]]](None)
  private val callbackRef = new AtomicReference[Option[Try[A] => _]](None)

  override def onComplete[U](f: (Try[A]) => U): Unit = {
    val oldCb = callbackRef.get()
    if (callbackRef.compareAndSet(oldCb, Some(f)) && valueRef.get().isDefined) {
      f(valueRef.get().get)
    }
  }

  def tryComplete(value: Try[A]): Unit = {
    if (valueRef.compareAndSet(None, Some(value)) && callbackRef.get().isDefined) {
      callbackRef.get().get(value)
    }
  }
}

object Promise {
  def apply[A](): Promise[A] = new Promise[A]
}
