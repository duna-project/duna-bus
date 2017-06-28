package io.duna.reflect

import scala.reflect.runtime.universe._
import scala.util.Try

object Types {
  implicit class TypeExtension(val self: Type) extends AnyVal {
    def clusterTypeName: String = self.toString
  }
}
