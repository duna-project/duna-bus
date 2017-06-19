package io.duna

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import scala.collection.immutable._
import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

object Playground extends App {

  val y = new Y

  val x3: AnyRef = new X[Map[List[Int], Set[String]]]
  val x1: AnyRef = y.createX()
  val x2: AnyRef = y.createX()

  val ttag1: AnyRef = y.createX().ttag
  val ttag2: AnyRef = y.createX().ttag

  val out = new ByteArrayOutputStream()

  val output = new ObjectOutputStream(out)

  output.writeObject(ttag1)

  println(out.toString)
  println(out.size())

  val input = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray))

  val arg = input.readObject().asInstanceOf[TypeTag[_]]

  // println(x1 == x2)
  println(ttag1 == arg)
}

class X[A](implicit val ttag: TypeTag[A])

class Y extends X[Map[List[Int], Set[String]]] {
  def createX(): X[Map[List[Int], Set[String]]] = new X[Map[List[Int], Set[String]]]
}

class Z[A](implicit val ctag: ClassTag[A])

class W extends Z[Map[List[Int], Set[String]]]