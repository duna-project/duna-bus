package io.duna.perf

import scala.reflect.runtime.universe._
import scala.reflect.runtime.{currentMirror, universe}
import scala.reflect.{ClassTag, api}
import scala.tools.reflect.ToolBox

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

@Fork(1)
@State(Scope.Thread)
class TypeTagBenchmark {

  val toolbox: ToolBox[universe.type] = currentMirror.mkToolBox()

  @Setup
  def setup(): Unit = {

  }

  @Benchmark
  def benchmarkTypeTagCreation(blackhole: Blackhole): Unit = {
    val x = toolbox.typecheck(tq"List[Int]", toolbox.TYPEmode).tpe

    val ttag: TypeTag[List[String]] = TypeTag(currentMirror, new api.TypeCreator {
      def apply[U <: api.Universe with Singleton](m: api.Mirror[U]): U#Type =
        if (m eq currentMirror) x.asInstanceOf[U # Type]
        else throw new IllegalArgumentException(s"Type tag defined in $currentMirror cannot be migrated to other mirrors.")
    })

    val x0 = new X[List[String]]()(ttag)

    blackhole.consume(x0)
  }

//  @Benchmark
  def benchmarkClassTagCreation(blackhole: Blackhole): Unit = {
    val y = new Y[Int]

    blackhole.consume(y.ttag)
  }
}

class X[A](implicit val ttag: TypeTag[A])

class Y[A](implicit val ttag: ClassTag[A])
