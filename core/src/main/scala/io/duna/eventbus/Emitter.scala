package io.duna.eventbus

import scala.reflect.ClassTag

trait Emitter[T] {

  def !(attachment: Option[T] = None): Unit

  def ?[V: ClassTag](attachment: Option[T] = None): Subscriber[V]

  def dispatch(attachment: Option[T]): Unit = this.!(attachment)

  def request[V: ClassTag](attachment: Option[T]): Subscriber[V] = this.?(attachment)

  def header(header: (String, String)): Emitter[T]

  // TODO Error handling?

}
