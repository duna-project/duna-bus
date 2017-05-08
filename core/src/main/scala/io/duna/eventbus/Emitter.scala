package io.duna.eventbus

import scala.reflect.ClassTag

trait Emitter[T] {

  def dispatch(attachment: Option[T] = None): Unit

  def expectReply[V: ClassTag](): Emitter[T] with Listener[V]

  def withHeader(header: (String, String)): Emitter[T]

}
