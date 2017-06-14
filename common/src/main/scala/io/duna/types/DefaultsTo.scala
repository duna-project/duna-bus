package io.duna.types

trait DefaultsTo[Type, Default]

object DefaultsTo {
  implicit def defaultDefaultsTo[T]: DefaultsTo[T, T] = null
  implicit def fallback[T, D]: DefaultsTo[T, D] = null
}
