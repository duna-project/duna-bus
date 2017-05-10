package io.duna.eventbus

import scala.reflect.ClassTag

import io.duna.eventbus.dsl.reply

trait Emitter[T] {

  def !(attachment: Option[T] = None): Emitter[T] = dispatch(attachment)

  def dispatch(attachment: Option[T] = None): Emitter[T]

  def expect[V: ClassTag](r: reply.type): ReplyableEmitter[T, V]

  def withHeader(header: (String, String)): Emitter[T]

}
