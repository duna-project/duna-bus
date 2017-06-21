package io.duna.s11n.json4s

import java.io.ByteArrayOutputStream

import org.json4s
import org.json4s.JsonAST._
import org.scalatest.FlatSpec

class Json4SObjectMapperSpec extends FlatSpec {

  behavior of classOf[Json4sObjectMapper].getName

  it must "serialize a custom class correctly" in {
    val mapper = new Json4sObjectMapper
    mapper.register[CustomClassCodec]

    val output = new ByteArrayOutputStream()
    val custom = new CustomClass(4, 5)

    mapper.serialize(custom, output)

    println(output.toString())
  }
}

class CustomClass(val x: Int, val y: Int)

class CustomClassCodec extends Json4SCodec[CustomClass] {

  override def serialize(value: CustomClass): json4s.JValue = {
    JObject(
      JField("x", JInt(value.x)) ::
        JField("y",   JInt(value.y)) :: Nil)
  }

  override def deserialize(serializedValue: json4s.JValue): CustomClass = serializedValue match {
    case JObject(JField("x", JInt(x)) :: JField("y", JInt(y)) :: Nil) =>
      new CustomClass(x.intValue(), y.intValue())
  }
}