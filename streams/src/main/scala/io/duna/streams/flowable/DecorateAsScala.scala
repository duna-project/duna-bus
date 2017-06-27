package io.duna.streams.flowable

import scala.language.implicitConversions

import Decorators._
import WrapAsScala._
import io.duna.streams.Flowable
import io.{reactivex => rx}

trait DecorateAsScala {

  implicit def asScalaFlowableConverter[A](f: rx.Flowable[A]): AsScala[Flowable[A]] =
    new AsScala(asScalaFlowable(f))

}
