package io.duna.eventbus.event

import scala.reflect.ClassTag

import com.twitter.util.Future
import io.duna.types.DefaultsTo

trait Emitter {

  def send[A: ClassTag](attachment: Option[A] = None)(implicit default: A DefaultsTo Unit): Unit

  def request[A: ClassTag, R: ClassTag](attachment: Option[A] = None)
                                       (implicit default: A DefaultsTo Unit): Future[Option[R]]

  def broadcast[A: ClassTag](attachment: Option[A] = None)(implicit default: A DefaultsTo Unit): Unit

  def complete(): Unit

  def withHeader(key: Symbol, value: String): Emitter

  def withHeader(header: (Symbol, String)): Emitter

}
