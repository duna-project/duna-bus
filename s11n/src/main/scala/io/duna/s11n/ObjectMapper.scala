package io.duna.s11n

import scala.reflect.runtime.universe.TypeTag

object ObjectMapper {

  def serialize[A <: AnyRef](obj: A): Array[Byte] = ???

  def deserialize[A <: AnyRef : TypeTag](input: Array[Byte]): Option[A] = ???

}
