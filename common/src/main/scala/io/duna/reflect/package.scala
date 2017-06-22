package io.duna

import scala.reflect.runtime.universe._

package object reflect {
  implicit class TypeExtension(val self: Type) extends AnyVal {
    def clusterTypeName: String = self.toString
  }
}
