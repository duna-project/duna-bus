package io.duna.s11n.json4s

import java.nio.ByteBuffer
import java.nio.charset.Charset

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.util.control.NonFatal

import io.duna.s11n.{Codec, ObjectMapper}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write

class Json4sObjectMapper extends ObjectMapper {

  implicit var formats: Formats = DefaultFormats

  def register[S <: Codec[_, _] : ClassTag]: Unit = {
    require(implicitly[ClassTag[S]].runtimeClass.isAssignableFrom(classOf[Json4SCodec[_]]),
      "The serializer must be a subclass of Json4sSerializer")

    val clazz: Class[S] = implicitly[ClassTag[S]].runtimeClass.asInstanceOf[Class[S]]
    val serializer = clazz.newInstance()

    formats.synchronized {
      formats = formats + serializer.asInstanceOf[Json4SCodec[_]].wrappedSerializer
    }
  }

  override def serialize[A <: AnyRef : TypeTag](value: A): ByteBuffer = {
    val serializedValue = write[A](value)
    ByteBuffer.wrap(serializedValue.getBytes(Json4sObjectMapper.CHARSET))
  }

  override def deserialize[A <: AnyRef : TypeTag](input: ByteBuffer): Option[A] = {
    val serializedBytes = Array.ofDim[Byte](input.remaining())
    input.get(serializedBytes)

    implicit val ctag = ClassTag[A](typeTag[A].mirror.runtimeClass(typeTag[A].tpe))

    try {
      val json = parse(new String(serializedBytes, Json4sObjectMapper.CHARSET))
      Some(json.extract[A])
    } catch {
      case NonFatal(_) => None
    }
  }
}

private[json4s] object Json4sObjectMapper {
  val CHARSET: Charset = Charset.forName("UTF-8")
}
