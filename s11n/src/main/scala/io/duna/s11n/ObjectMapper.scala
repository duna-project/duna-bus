package io.duna.s11n

import java.util.concurrent.ConcurrentHashMap

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

import io.duna.s11n.codec.Codec

object ObjectMapper {

  private val registeredCodecs = new ConcurrentHashMap[Class[_], Codec[_]]()

  def serialize[A <: AnyRef](obj: A): Array[Byte] = ???

  def deserialize[A <: AnyRef : TypeTag](input: Array[Byte]): Option[A] = ???

  def register[S <: Codec[_] : ClassTag]: ObjectMapper.type = {
    val codec = implicitly[ClassTag[S]].runtimeClass.newInstance().asInstanceOf[Codec[S]]
    registeredCodecs.put(implicitly[ClassTag[S]].runtimeClass, codec)

    this
  }

}
