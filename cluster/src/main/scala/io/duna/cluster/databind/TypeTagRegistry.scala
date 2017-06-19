package io.duna.cluster.databind

import java.util.concurrent.ConcurrentHashMap

import scala.reflect.api
import scala.reflect.runtime.universe._

object TypeTagRegistry {

  private val registry = new ConcurrentHashMap[String, TypeTag[_]]

  def register(name: String, typeTag: TypeTag[_]): Unit = {
    registry.put(name, typeTag)
  }
}
