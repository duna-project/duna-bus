package io.duna.reflect

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._
import scala.reflect.api
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBox
import scala.util.control.NonFatal

import io.duna.collection.NonBlockingHashMapLong

object TypeTagCache {

  private val toolbox = currentMirror.mkToolBox()
  private val cache = new NonBlockingHashMapLong[TypeTag[_]]()

  def put(typeTag: TypeTag[_]): Unit = {
    cache.put(typeTag.toString().hashCode(), typeTag)
  }

  def get(name: String): TypeTag[_] = {
    Option(cache.get(name.hashCode)) match {
      case Some(ttag) => ttag
      case None =>
        try {
          val typeTagCall = s"scala.reflect.runtime.universe.typeTag[$name]"
          val tpe = toolbox.typecheck(toolbox.parse(typeTagCall), toolbox.TYPEmode).tpe.resultType.typeArgs.head

          val ttag: TypeTag[List[String]] = TypeTag(currentMirror, new api.TypeCreator {
            def apply[U <: api.Universe with Singleton](m: api.Mirror[U]): U#Type =
              if (m == currentMirror) tpe.asInstanceOf[U#Type]
              else throw new IllegalArgumentException(
                s"Type tag defined in $currentMirror cannot be migrated to other mirrors.")
          })

          cache.put(name.hashCode, ttag)
          ttag
        } catch {
          case NonFatal(_) => throw new RuntimeException
        }
    }
  }
}
