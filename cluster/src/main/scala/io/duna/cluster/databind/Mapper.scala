package io.duna.cluster.databind

import java.nio.ByteBuffer

import scala.reflect.runtime.universe.TypeTag

object Mapper {

  def register[A: TypeTag](serializer: Serializer[A]): Unit = ???

  def register[A: TypeTag](deserializer: Deserializer[A]): Unit = ???

  def serialize[A: TypeTag](obj: A): ByteBuffer = ???

  def deserialize[A: TypeTag](bytes: ByteBuffer): A = ???

}
