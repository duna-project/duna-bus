package io.duna.perf

import java.util
import java.util.UUID
import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.parallel.mutable.ParMap
import scala.concurrent.stm._

import io.duna.collection.{NonBlockingHashMap, NonBlockingHashMapLong}
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@Threads(5)
class MapBenchmark {

  @Param(Array("SynchronizedHashMap",
    "ConcurrentHashMap",
    "NonBlockingHashMap",
    "NonBlockingHashMapLong"))
  private var mapType: String = _

  private var map: mutable.Map[java.lang.Long, String] = _

  private val values1: Array[String] = Array.ofDim[String](256)
  private val values2: Array[String] = Array.ofDim[String](256)

  @Setup
  def setup(): Unit = {
    mapType match {
      case "SynchronizedHashMap" => map = new mutable.HashMap[java.lang.Long, String] with mutable.SynchronizedMap[java.lang.Long, String]
      case "ConcurrentHashMap" => map = new ConcurrentHashMap[java.lang.Long, String].asScala
      case "NonBlockingHashMap" => map = new NonBlockingHashMap[java.lang.Long, String].asScala
      case "NonBlockingHashMapLong" => map = new NonBlockingHashMapLong[String].asScala
    }

    for (i <- 1 until 256) {
      values1(i) = UUID.randomUUID().toString
      values2(i) = UUID.randomUUID().toString
    }
  }

  @Benchmark
  def benchmark(blackhole: Blackhole): Unit = {
    // Populate
    for (i <- 1L until 256L) {
      map(i) = values1(i.toInt)
    }

    // Replace
    for (i <- 1L until 256L) {
      map(i) = values2(i.toInt)
    }

    // Read
    for (i <- 1L until 256L) {
      blackhole.consume(map(i))
    }
  }
}
