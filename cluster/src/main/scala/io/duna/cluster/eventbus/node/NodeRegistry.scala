package io.duna.cluster.eventbus.node

import java.net.InetSocketAddress

import scala.reflect.runtime.universe.TypeTag

import io.duna.eventbus.EventBus

trait NodeRegistry {

  val eventBus: EventBus

  def register[A: TypeTag](event: String): Unit

  def unregister[A: TypeTag](event: String): Unit

  def list[A: TypeTag](event: String): List[InetSocketAddress]

  def next[A: TypeTag](event: String): InetSocketAddress
}
