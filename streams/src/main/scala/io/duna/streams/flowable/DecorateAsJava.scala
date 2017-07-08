package io.duna.streams.flowable

import scala.language.implicitConversions

import Decorators._
import WrapAsJava._
import io.duna.streams.Flowable
import io.{reactivex => rx}

trait DecorateAsJava {
  implicit def asJavaFlowableConverter[A](f: Flowable[A]): AsJava[rx.Flowable[A]] =
    new AsJava(asJavaFlowable(f))
}
