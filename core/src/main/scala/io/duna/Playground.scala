package io.duna

import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

object Playground extends App {

}

class X[A](implicit val ttag: TypeTag[A]) {
  def isListOfInt: Boolean  = typeOf[A] match {
    case t if t =:= typeOf[List[Int]] => true
    case _ => false
  }
}

object XFactory {
  def create(): X[Map[List[Int], Set[String]]] = new X[Map[List[Int], Set[String]]]
}

class Y extends X[Map[List[Int], Set[String]]] {
  def createX(): X[Map[List[Int], Set[String]]] = new X[Map[List[Int], Set[String]]]
}

class Z[A](implicit val ctag: ClassTag[A])

class W extends Z[Map[List[Int], Set[String]]]