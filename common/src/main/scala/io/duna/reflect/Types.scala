package io.duna.reflect

import scala.reflect.runtime.universe._
import scala.util.Try

object Types {

  def classOf(name: String): Option[Class[_]] = Try(name match {
    // Predef types
    case "Byte" =>    Predef.classOf[Byte]
    case "Char" =>    Predef.classOf[Char]
    case "Short" =>   Predef.classOf[Short]
    case "Int" =>     Predef.classOf[Int]
    case "Long" =>    Predef.classOf[Long]
    case "Float" =>   Predef.classOf[Float]
    case "Double" =>  Predef.classOf[Double]

    case "String" => Predef.classOf[String]

    case "List" =>  Predef.classOf[List[_]]
    case "Map" =>   Predef.classOf[Map[_, _]]
    case "Set" =>   Predef.classOf[Set[_]]

    case n => Class.forName(name)
  }).toOption

  def typeTag(name: String): Option[TypeTag[_]] = {
    null
  }
}
