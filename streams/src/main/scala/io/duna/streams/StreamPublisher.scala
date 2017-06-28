package io.duna.streams

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import scala.annotation.tailrec
import scala.reflect.runtime.universe.TypeTag
import scala.collection.JavaConverters.asScalaSet

import io.duna.eventbus.EventBus
import io.duna.eventbus.event.Listener
import org.reactivestreams.{Publisher, Subscriber, Subscription}

class StreamListener[A: TypeTag](val event: String)
                                (implicit eventBus: EventBus) extends Listener[A] with Publisher[A] {

  private val subscriptions: AtomicReference[Set[StreamSubscription[_ >: A]]] = new AtomicReference(Set.empty)

  override def subscribe(s: Subscriber[_ >: A]): Unit = {
    trySubscribe(new StreamSubscription[A](s, this))
    eventBus route[A] event to this
  }

  override def onNext(value: Option[_ <: A]): Unit = value match {
    case Some(v) => subscriptions.get()
      .map(s => s.subscriber)
      .foreach(s => s.onNext(v))
    case None =>
  }

  override def onError(t: Throwable): Unit =
    subscriptions.get()
      .map(s => s.subscriber)
      .foreach(s => s.onError(t))

  override def onComplete(): Unit =
    subscriptions.get()
      .map(s => s.subscriber)
      .foreach(s => s.onComplete())

  @tailrec
  private def trySubscribe(subscription: StreamSubscription[_ >: A], attempts: Int = 0): Boolean = {
    if (attempts == 10000) return false

    val oldSubs = subscriptions.get()
    val newSubs = oldSubs + subscription

    if (!subscriptions.compareAndSet(oldSubs, newSubs))
      trySubscribe(subscription)
    else true
  }

  @tailrec
  private[streams] def tryUnsubscribe(subscription: StreamSubscription[_ >: A], attempts: Int = 0): Boolean = {
    if (attempts == 10000) return false

    val oldSubs = subscriptions.get()
    val newSubs = oldSubs - subscription

    if (!subscriptions.compareAndSet(oldSubs, newSubs))
      tryUnsubscribe(subscription)
    else if (newSubs.isEmpty)
      eventBus tryUnroute (event, this)
    else true
  }
}

class StreamSubscription[A](private[streams] val subscriber: Subscriber[_ >: A],
                            private[streams] val publisher: StreamListener[A])
  extends Subscription {

  override def cancel(): Unit = publisher.tryUnsubscribe(this)

  override def request(n: Long): Unit = ???
}