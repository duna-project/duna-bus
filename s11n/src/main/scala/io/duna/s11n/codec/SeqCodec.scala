package io.duna.s11n.codec
import java.io.ByteArrayOutputStream

import scala.reflect.runtime.universe

import io.duna.s11n.ObjectMapper

object SeqCodec extends Codec[Seq[_]] {

  override val sourceType: universe.TypeTag[Seq[_]] = _

  override def serialize(value: Seq[_]): Array[Byte] = {
    val output = new ByteArrayOutputStream()

    value.foreach { v =>
      ObjectMapper.serialize(v)
    }

    output.toByteArray
  }

  override def deserialize(bytes: Array[Byte]): Seq[_] = ???
}
