package io.duna.concurrent.future

import scala.util.Try

trait Future[+A] {

  def onComplete[U](f: Try[A] => U): Unit

}
