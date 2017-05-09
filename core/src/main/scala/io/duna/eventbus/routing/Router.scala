package io.duna.eventbus.routing

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec
import scala.reflect.ClassTag

import io.duna.eventbus.Listener
import io.duna.eventbus.message.Message

final class Router {

  private val persistentListeners =
    new ConcurrentHashMap[String, AtomicReference[List[Listener[_]]]]()

  private val disposableListeners = new ConcurrentHashMap[String, Listener[_]]()

  def <~>[T: ClassTag](event: String): Route[T] = route[T](event)

  def -=(route: Listener[_]): Unit = remove(route)

  def --=(event: String): Unit = removeAll(event)

  def route[T: ClassTag](event: String): Route[T] = new Route[T](event, this)

  def remove(listener: Listener[_]): Unit = removeRoute(listener)

  def removeAll(event: String): Unit = persistentListeners.remove(event)

  def matching[T](message: Message[T]): List[Listener[_]] = {
    if (disposableListeners.containsKey(message.target)) {
      val result = disposableListeners.remove(message.target)
      return List(result)
    }

    Option(persistentListeners.get(message.target)) match {
      case None => List.empty
      case Some(ref) => ref
        .get()
        .filter(_ matches message)
    }
  }

  private[routing] def registerDisposableRoute(listener: Listener[_]): Unit = {
    if (persistentListeners.containsKey(listener.event))
      throw new IllegalArgumentException("A disposable listener must listen to a distinct event.")

    if (disposableListeners.putIfAbsent(listener.event, listener) != null)
      throw new IllegalStateException("Another disposable listener is already mapped to the event provided.")
  }

  @tailrec private[routing] def registerRoute(listener: Listener[_]): Unit = {
    if (disposableListeners.containsKey(listener.event))
      throw new IllegalArgumentException("A disposable event cannot be mapped to a persistent listener.")

    val ref = persistentListeners.computeIfAbsent(listener.event,
      _ => {
        new AtomicReference(List.empty)
      })

    val oldList = ref.get()
    val newList = oldList :+ listener

    if (!ref.compareAndSet(oldList, newList)) {
      registerRoute(listener)
    } else {
      val newValue = persistentListeners.computeIfPresent(listener.event, { (_, currRef) =>
        if (currRef == ref) ref else currRef
      })

      if (newValue != ref) registerRoute(listener)
    }
  }

  @tailrec private[routing] def removeRoute(listener: Listener[_]): Unit = {
    val ref = persistentListeners.computeIfAbsent(listener.event,
      _ => {
        new AtomicReference(List.empty)
      })

    val oldList = ref.get()
    val newList = oldList.filter(_ != listener)

    if (!ref.compareAndSet(oldList, newList)) {
      removeRoute(listener)
    } else {
      val newValue = persistentListeners.computeIfPresent(listener.event, { (_, currRef) =>
        if (currRef == ref) ref else currRef
      })

      if (newValue != null && newValue != ref)
        removeRoute(listener)
    }
  }
}

class Route[T: ClassTag](val event: String,
                         val router: Router) {

  def to(listener: Listener[T]): Unit = {
    if (listener == null) throw new NullPointerException
    router.registerRoute(listener)
  }

  def onceTo(listener: Listener[_]): Unit = {
    if (listener == null) throw new NullPointerException
    router.registerDisposableRoute(listener)
  }
}


