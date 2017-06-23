package io.duna.s11n.codec

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.TypeTag

trait Codec[A <: AnyRef] {

  val sourceType: TypeTag[A]

  def serialize(value: A): Array[Byte]

  def deserialize(bytes: Array[Byte]): A

}
