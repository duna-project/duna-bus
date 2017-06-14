package io.duna.eventbus.message

import scala.reflect.ClassTag
import scala.util.Try

import io.duna.eventbus.Context

protected[duna]
abstract case class Message[A: ClassTag](target: String,
                                         responseEvent: Option[String] = None,
                                         headers: Map[Symbol, String] = Map.empty,
                                         attachment: Option[A] = None,
                                         transmissionMode: TransmissionMode) {

  val source: Option[String] = Try(Context.current.currentEvent).toOption

  lazy val classTag: ClassTag[A] = implicitly[ClassTag[A]]

  lazy val attachmentType: Class[A] = classTag.runtimeClass.asInstanceOf[Class[A]]

}