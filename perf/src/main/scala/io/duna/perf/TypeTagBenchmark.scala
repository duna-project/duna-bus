package io.duna.perf

import scala.tools.reflect.ToolBox
import scala.reflect.runtime.universe._

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

@Fork(2)
@State(Scope.Benchmark)
class TypeTagBenchmark {

  // implicit var ttag: TypeTag[Map[List[Int], Set[String]]] = _

  implicit var ctag: ClassTag[Map[List[Int], Set[String]]] = _

  var toolbox: ToolBox = _

  @Setup
  def setup(): Unit = {
//    ttag = typeTag[Map[List[Int], Set[String]]]
    y = new Y
    ctag = new W().ctag
  }

  @Benchmark
  def benchmarkTypeTagCreation(blackhole: Blackhole): Unit = {
    val obj = y.createX()

    blackhole.consume(obj)
  }

//  @Benchmark
  def benchmarkClassTagCreation(blackhole: Blackhole): Unit = {
    val obj = new Z[Map[List[Int], Set[String]]]()(ctag)

    blackhole.consume(obj)
  }
}

class X[A](implicit val ttag: TypeTag[A])

class Y extends X[Map[List[Int], Set[String]]] {
  def createX(): X[Map[List[Int], Set[String]]] = new X[Map[List[Int], Set[String]]]
}

class Z[A](implicit val ctag: ClassTag[A])

class W() extends Z[Map[List[Int], Set[String]]]