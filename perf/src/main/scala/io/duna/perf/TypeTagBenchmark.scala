package io.duna.perf

import java.util
import java.util.concurrent.ConcurrentHashMap

import scala.reflect.runtime.universe._
import scala.reflect.runtime.{currentMirror, universe}
import scala.reflect.{ClassTag, api}
import scala.tools.reflect.ToolBox

import net.openhft.chronicle.map.{ChronicleMap, ChronicleMapBuilder}
import org.cliffc.high_scale_lib.{NonBlockingHashMap, NonBlockingHashMapLong}
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.runner.Runner

@Fork(1)
@State(Scope.Thread)
class TypeTagBenchmark {

  val toolbox: ToolBox[universe.type] = currentMirror.mkToolBox()

  val x = toolbox.typecheck(tq"List[Int]", toolbox.TYPEmode).tpe

  val ttag: TypeTag[_] = TypeTag(currentMirror, new api.TypeCreator with Serializable {
    def apply[U <: api.Universe with Singleton](m: api.Mirror[U]): U#Type =
      if (m eq currentMirror) x.asInstanceOf[U#Type]
      else throw new IllegalArgumentException(s"Type tag defined in $currentMirror cannot be migrated to other mirrors.")
  })

  val map: util.Map[java.lang.Integer, universe.TypeTag[_]] =
    ChronicleMapBuilder.of(classOf[java.lang.Integer], classOf[TypeTag[_]])
      .name("type-tags")
      .averageValueSize(195)
      .entries(1)
      .create()

  val ttagString = ttag.toString().hashCode
  map.put(ttag.toString().hashCode, ttag)

  @Setup
  def setup(): Unit = {

  }

  @Benchmark
  def benchmarkTypeTagCreation(blackhole: Blackhole): Unit = {
    blackhole.consume(map.get(ttagString))
  }

  @Benchmark
  def benchmarkTypeTagCreation2(blackhole: Blackhole): Unit = {

  }

  @Benchmark
  def benchmarkClassTagCreation(blackhole: Blackhole): Unit = {
    val y = new Y[Int]

    blackhole.consume(y.ttag)
  }
}

class X[A](implicit val ttag: TypeTag[A])

class Y[A](implicit val ttag: ClassTag[A])

object Start extends App {

  val map: util.Map[java.lang.Long, universe.TypeTag[_]] = new NonBlockingHashMapLong[universe.TypeTag[_]]()
//    ChronicleMapBuilder.of(classOf[java.lang.Integer], classOf[TypeTag[_]])
//      .name("type-tags")
//      .averageValueSize(195)
//      .entries(1)
//      .create()

  val toolbox: ToolBox[universe.type] = currentMirror.mkToolBox()

  val x = toolbox.typecheck(tq"List[Int]", toolbox.TYPEmode).tpe

  val ttag: TypeTag[_] = TypeTag(currentMirror, new api.TypeCreator {
    def apply[U <: api.Universe with Singleton](m: api.Mirror[U]): U#Type =
      if (m eq currentMirror) x.asInstanceOf[U#Type]
      else throw new IllegalArgumentException(s"Type tag defined in $currentMirror cannot be migrated to other mirrors.")
  })

  val hashcode = ttag.toString().hashCode.toLong

  map.put(hashcode, ttag)

  val start = System.currentTimeMillis()

  for (_ <- 0 to 100000) yield map.get(hashcode)

  println(System.currentTimeMillis() - start)
}