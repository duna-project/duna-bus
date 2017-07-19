package io.duna.eventbus.event

import scala.reflect.runtime.universe.TypeTag

import io.duna.concurrent.future.Future
import io.duna.types.DefaultsTo

trait Emitter {

  def send[A: TypeTag](attachment: Option[A] = None): Unit

  def request[A: TypeTag, R: TypeTag](attachment: Option[A] = None)
                                       (implicit default: A DefaultsTo Unit): Future[Option[R]]

  def broadcast[A: TypeTag](attachment: Option[A] = None): Unit

  def complete(): Unit

  def withHeader(key: Symbol, value: String): Emitter

  def withHeader(header: (Symbol, String)): Emitter

}
