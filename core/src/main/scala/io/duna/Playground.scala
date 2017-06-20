package io.duna

import scala.reflect.runtime.universe._

object Playground extends App {

  println(new X[List[Int]]().ttag)
}

class X[A](implicit val ttag: TypeTag[A]) {
}
