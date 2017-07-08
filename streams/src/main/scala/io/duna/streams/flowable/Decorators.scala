package io.duna.streams.flowable

private[streams] trait Decorators {
  class AsJava[A](op: => A) {
    def asJava: A = op
  }

  class AsScala[A](op: => A) {
    def asScala: A = op
  }
}

private[streams] object Decorators extends Decorators
