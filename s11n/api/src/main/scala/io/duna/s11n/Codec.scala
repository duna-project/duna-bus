package io.duna.s11n

import scala.reflect.runtime.universe.TypeTag

trait Codec[A, B] {

  val sourceType: TypeTag[A]

  val targetType: TypeTag[B]

  def serialize(value: A): B

  def deserialize(serializedValue: B): A

}
