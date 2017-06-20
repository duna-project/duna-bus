package io.duna.eventbus.message

import scala.reflect.runtime.universe.TypeTag
import scala.util.Try

import io.duna.eventbus.Context

protected[duna]
abstract case class Message[A: TypeTag](source: Option[String] = Try(Context.current.currentEvent).toOption,
                                        target: String,
                                        responseEvent: Option[String] = None,
                                        headers: Map[Symbol, String] = Map.empty,
                                        attachment: Option[A] = None,
                                        transmissionMode: TransmissionMode) {

  lazy val typeTag: TypeTag[A] = implicitly[TypeTag[A]]

  lazy val attachmentType: Class[A] = typeTag.mirror.runtimeClass(typeTag.tpe).asInstanceOf[Class[A]]

}