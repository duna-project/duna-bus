package io.duna.reflect

import java.util.concurrent.ConcurrentHashMap

import scala.reflect.api
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBox
import scala.util.control.NonFatal

object TypeTagCache {

  private val toolbox = currentMirror.mkToolBox()
  private val cache = new ConcurrentHashMap[String, TypeTag[_]]()

  def put(typeTag: TypeTag[_]): TypeTag[_] = {
    cache.put(typeTag.toString(), typeTag)
  }

  def get(name: String): Option[TypeTag[_]] = {
    println(name)

    val currentValue = cache.get(name)

    if (currentValue != null) return Some(currentValue)

    try {
      val typeTagCall = s"scala.reflect.runtime.universe.typeTag[$name]"
      val tpe = toolbox.typecheck(toolbox.parse(typeTagCall), toolbox.TYPEmode).tpe.resultType.typeArgs.head

      val ttag: TypeTag[List[String]] = TypeTag(currentMirror, new api.TypeCreator {
        def apply[U <: api.Universe with Singleton](m: api.Mirror[U]): U#Type =
          if (m eq currentMirror) tpe.asInstanceOf[U#Type]
          else throw new IllegalArgumentException(
            s"Type tag defined in $currentMirror cannot be migrated to other mirrors.")
      })

      Some(cache.computeIfAbsent(name, _ => ttag))
    } catch {
      case NonFatal(_) => None
    }
  }
}