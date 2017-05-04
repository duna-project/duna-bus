package io.duna.collection.concurrent

import java.util.concurrent.ConcurrentHashMap
import java.util.{concurrent => juc}
import scala.collection.JavaConverters._
import scala.collection.mutable

class ConcurrentHashMultimap[K, V] {

  private val underlying = new juc.ConcurrentHashMap[K, mutable.Set[V]]().asScala

  def +=(kv: (K, V)): ConcurrentHashMultimap[K, V] = {
    val values = underlying.getOrElseUpdate(kv._1, ConcurrentHashMap.newKeySet[V]().asScala)
    values += kv._2

    this
  }

  def -=(key: K): Boolean = get(key) match {
    case Some(k) =>
      k.clear()
      true
    case None =>
      false
  }

  def --=(kv: (K, V)): Boolean = get(kv._1) match {
    case Some(k) => k.remove(kv._2)
    case None => false
  }

  def get(key: K): Option[mutable.Set[V]] = underlying.get(key)

  def getOrElseUpdate(key: K, op: => V): V = {
    val values = underlying.getOrElseUpdate(key, ConcurrentHashMap.newKeySet[V]().asScala)
    values += op
    op
  }

  def iterator: Iterator[(K, mutable.Set[V])] = underlying.iterator
}

object ConcurrentHashMultimap {
  def apply[K, V]() = new ConcurrentHashMultimap[K, V]()
}
