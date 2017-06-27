package io.duna.streams

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

import scala.reflect.runtime.universe.TypeTag
import scala.collection.JavaConverters.asScalaSet

import io.duna.eventbus.EventBus
import io.duna.eventbus.event.Listener
import org.reactivestreams.{Publisher, Subscriber, Subscription}

class StreamListener[A: TypeTag](val event: String)
                                (implicit eventBus: EventBus) extends Listener[A] with Publisher[A] {

  private val subscriptions = asScalaSet(ConcurrentHashMap.newKeySet[StreamSubscription[_ >: A]])
  private var active: Boolean = false

  private object lock

  override def subscribe(s: Subscriber[_ >: A]): Unit = {
    if (!active) lock.synchronized {
      eventBus route[A] event to this
      active = true
    }

    subscriptions.add(new StreamSubscription[A](s, this))
  }

  override def onNext(value: Option[_ <: A]): Unit = value match {
    case Some(v) => subscriptions
      .map(s => s.subscriber)
      .foreach(s => s.onNext(v))
    case None =>
  }

  override def onError(t: Throwable): Unit =
    subscriptions
      .map(s => s.subscriber)
      .foreach(s => s.onError(t))

  override def onComplete(): Unit =
    subscriptions
      .map(s => s.subscriber)
      .foreach(s => s.onComplete())


  private[streams] def unsubscribe(subscription: StreamSubscription[_ >: A]) = {
    subscriptions.remove(subscription)

    if (subscriptions.isEmpty && active) lock.synchronized {
      eventBus unroute (event, this)
      active = false
    }
  }
}

class StreamSubscription[A](private[streams] val subscriber: Subscriber[_ >: A],
                            private[streams] val publisher: StreamListener[A])
  extends Subscription {

  override def cancel(): Unit = publisher.unsubscribe(this)

  override def request(n: Long): Unit = ???
}