package io.duna.s11n.json4s

import scala.reflect.ClassTag
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

import io.duna.s11n.Codec
import org.json4s.{CustomSerializer, JValue}

abstract class Json4SCodec[A](implicit override val sourceType: universe.TypeTag[A],
                              override val targetType: universe.TypeTag[JValue])
  extends Codec[A, JValue] {

  implicit val ctag: ClassTag[A] = ClassTag(typeTag[A].mirror.runtimeClass(typeOf[A]))

  private[json4s] final val wrappedSerializer = new CustomSerializer[A](_ => (
    PartialFunction { v: JValue => deserialize(v) },
    PartialFunction { case a: A => serialize(a) }))

}
