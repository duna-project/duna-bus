package io.duna.cluster.databind

trait Serializer[-A] {

  def serialize(value: A): Array[Byte]
}
