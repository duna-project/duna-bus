package io.duna.streams.flowable

import scala.language.implicitConversions

import io.duna.streams.Flowable
import io.{reactivex => rx}

trait WrapAsJava {
  implicit def asJavaFlowable[A](flowable: Flowable[A]): rx.Flowable[A] =
    flowable.javaFlowable
}

object WrapAsJava extends WrapAsJava
