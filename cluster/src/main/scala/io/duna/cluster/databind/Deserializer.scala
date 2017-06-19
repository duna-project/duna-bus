package io.duna.cluster.databind

trait Deserializer[+A] {

  def deserialize(bytes: Array[Byte]): A
}
