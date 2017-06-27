package io.duna.concurrent.future

import scala.concurrent.Awaitable
import scala.util.Try

trait Future[+T] extends Awaitable[T] {

  def onComplete[U](f: Try[T] => U): Unit

}
