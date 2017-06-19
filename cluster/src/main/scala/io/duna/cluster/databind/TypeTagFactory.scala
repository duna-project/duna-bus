package io.duna.cluster.databind

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._
import scala.reflect.runtime.universe._
import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox
import scala.util.{Failure, Success, Try}

object TypeTagFactory {

  def apply(typeName: String): Option[TypeTag[_]] = ???
}
