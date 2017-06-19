package io.duna.cluster.reflect

import scala.util.Try
import scala.reflect.runtime.universe._
import scala.reflect.runtime.{currentMirror, universe}
import scala.tools.reflect.ToolBox
import java.util.concurrent.ConcurrentHashMap

/**
  * Created by eduribeiro on 19/06/2017.
  */
object TypeTagCache {

  private val toolbox = currentMirror.mkToolBox()
  private val cache = new ConcurrentHashMap[String, TypeTag[_]]()

  def fetch(name: String): Option[TypeTag[_]] = {
    val currentValue = cache.get(name)

    if (currentValue != null) return Some(currentValue)

    val tree = toolbox.parse(name)
    tree match {
      case TypeApply(_, _) =>

      case _ => None
    }
  }

  object traverser extends Traverser {

    override def traverse(tree: universe.Tree): Unit = {
      case typApply @ TypeApply(fun, args) =>

      case _ => throw new RuntimeException("Cannot traverse the tree provided.")
    }
  }
}
