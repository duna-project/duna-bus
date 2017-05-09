package io.duna.eventbus.routing

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec
import scala.reflect.ClassTag

import io.duna.eventbus.Listener
import io.duna.eventbus.message.Message

final class Router {

  private val eventToListenersMapping =
    new ConcurrentHashMap[String, AtomicReference[List[Listener[_]]]]()

  def <->[T: ClassTag](event: String): Route[T] = route[T](event)

  def -/>(event: String): Unit = removeAll(event)

  def -/>(route: Listener[_]): Unit = remove(route)

  def route[T: ClassTag](event: String): Route[T] = new Route[T](event, this)

  def remove(listener: Listener[_]): Unit = removeRoute(listener)

  def removeAll(event: String): Unit = eventToListenersMapping.remove(event)

  def matching[T](message: Message[T]): List[Listener[_]] =
    Option(eventToListenersMapping.get(message.target)) match {
      case None => List.empty
      case Some(ref) => ref
        .get()
        .filter(_ matches message)
    }

  @tailrec
  private[routing] def registerRoute(listener: Listener[_]): Unit = {
    val ref = eventToListenersMapping.computeIfAbsent(listener.event,
      _ => {
        new AtomicReference(List.empty)
      })

    var oldList = ref.get()
    var newList = oldList :+ listener

    while (!ref.compareAndSet(oldList, newList)) {
      oldList = ref.get()
      newList = oldList :+ listener
    }

    val newValue = eventToListenersMapping.computeIfPresent(listener.event, { (_, currRef) =>
      if (currRef == ref) ref
      else currRef
    })

    if (newValue != ref)
      registerRoute(listener)
  }

  @tailrec private[routing] def removeRoute(listener: Listener[_]): Unit = {
    val ref = eventToListenersMapping.computeIfAbsent(listener.event,
      _ => {
        new AtomicReference(List.empty)
      })

    var oldList = ref.get()
    var newList = oldList.filter(_ != listener)

    while (!ref.compareAndSet(oldList, newList)) {
      oldList = ref.get()
      newList = oldList.filter(_ != listener)
    }

    val newValue = eventToListenersMapping.computeIfPresent(listener.event, { (_, v) =>
      if (v == ref) ref
      else v
    })

    if (newValue != null && newValue != ref)
      removeRoute(listener)
  }
}

class Route[T: ClassTag](val event: String,
                         val router: Router) {

  def to(listener: Listener[T]): Unit = {
    if (listener == null) throw new NullPointerException
    router.registerRoute(listener)
  }
}


