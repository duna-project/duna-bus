package io.duna.cluster

import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox
import scala.reflect.runtime.universe._
import scala.util.{Failure, Success, Try}

package object databind {

  private val toolBox = currentMirror.mkToolBox()

  def typeTag(typeName: String): Option[TypeTag[_]] = {
    val parseTree = toolBox.parse(s"scala.reflect.runtime.universe.typeTag[$typeName]")

    Try(toolBox.eval(parseTree).asInstanceOf[TypeTag[_]]) match {
      case Success(ttag) => Some(ttag)
      case Failure(_) => None
    }
  }
}
