package io.duna.streams.flowable

import scala.language.implicitConversions

import io.duna.streams.Flowable
import io.{reactivex => rx}

trait WrapAsScala {
  implicit def asScalaFlowable[A](flowable: rx.Flowable[A]): Flowable[A] =
    new Flowable[A] {
      override private[streams] val javaFlowable = flowable
    }
}

object WrapAsScala extends WrapAsScala
