package io.duna.perf

import java.util
import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.parallel.mutable.ParMap
import scala.concurrent.stm._

import io.duna.collection.{NonBlockingHashMap, NonBlockingHashMapLong}
import org.cliffc.high_scale_lib.NonBlockingHashMap
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

@Fork(2)
@Threads(8)
@State(Scope.Benchmark)
class MapBenchmark {

  var map: mutable.Map[Int, String] = _
  var chmap: ConcurrentHashMap[Int, String] = _
  var parmap: ParMap[Int, String] = _
  var nbmap: mutable.Map[Int, String] = _
  var nbmapspec: NonBlockingHashMapLong[String] = _
  var tmap: TMap[Int, String] = _

  object lock

  @Setup
  def setup(): Unit = {
    map = new mutable.HashMap[Int, String]()
    chmap = new ConcurrentHashMap[Int, String]()
    parmap = ParMap.empty[Int, String]
    nbmap = new NonBlockingHashMap[Int, String]().asScala
    nbmapspec = new NonBlockingHashMapLong[String]()
    tmap = TMap()
  }

  @Benchmark
  def benchSyncMap(blackhole: Blackhole): Unit = {
    val key: Int = 10
    map.synchronized {
      map.remove(key)
      map.put(key, "asd")
      blackhole.consume(map.get(key))
    }
  }

  @Benchmark
  def benchConcurrentMap(blackhole: Blackhole): Unit = {
    val key: Int = 10
    chmap.remove(key)
    chmap.put(key, "asd")
    blackhole.consume(chmap.get(key))
  }

  @Benchmark
  def benchParallelMap(blackhole: Blackhole): Unit = {
    val key: Int = 10
    parmap -= key
    parmap += key -> "asd"
    blackhole.consume(parmap.getOrElse(key, "asd"))
  }

  @Benchmark
  def benchNbConcurrentMap(blackhole: Blackhole): Unit = {
    val key: Int = 10
    nbmap.remove(key)
    nbmap.put(key, "asd")
    blackhole.consume(nbmap.get(key))
  }

  @Benchmark
  def benchNbSpecConcurrentMap(blackhole: Blackhole): Unit = {
    val key: Long = 10L
    nbmapspec.remove(key)
    nbmapspec.put(key, "asd")
    blackhole.consume(nbmapspec.get(key))
  }

  @Benchmark
  def benchLock(blackhole: Blackhole): Unit = {
    lock synchronized {
      Blackhole.consumeCPU(1)
    }
  }

  @Benchmark
  def benchControl(blackhole: Blackhole): Unit = {
    Blackhole.consumeCPU(1)
  }
}
