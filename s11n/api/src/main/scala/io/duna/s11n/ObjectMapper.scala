package io.duna.s11n

import java.nio.ByteBuffer
import java.util.ServiceLoader

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

//noinspection UnitMethodIsParameterless
trait ObjectMapper {

  def serialize[A <: AnyRef : TypeTag](obj: A): ByteBuffer

  def deserialize[A <: AnyRef : TypeTag](input: ByteBuffer): Option[A]

  def register[S <: Codec[_, _] : ClassTag]: Unit

}

object ObjectMapper {

  private val serviceLoader = ServiceLoader.load(classOf[ObjectMapper])
  private var service: ObjectMapper = _

  def apply(): ObjectMapper = {
    if (service == null) {
      serviceLoader.reload()
      service =
        if (serviceLoader.iterator().hasNext)
          serviceLoader.iterator().next()
        else
          throw new RuntimeException("No ObjectMapper implementations available.")
    }

    service
  }
}
