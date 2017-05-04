package io.duna.eventbus.messaging

import scala.reflect.ClassTag

protected[eventbus] trait MessageConsumer[T] {

  def enqueue[V: ClassTag](message: Message[V])

  def consume[V: ClassTag](message: Message[V])

}
