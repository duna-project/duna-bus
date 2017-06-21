package io.duna.eventbus

import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}

import scala.util.{Failure, Success}

import io.duna.concurrent.MultiThreadEventLoopGroup
import io.duna.eventbus.dsl._
import io.duna.eventbus.errors.NoRouteFoundException
import io.duna.eventbus.event.{DefaultListener, Listener}
import org.scalatest._
import org.scalatest.concurrent.Waiters
import org.scalatest.time.SpanSugar._

class LocalEventBusSpec extends FlatSpec
  with BeforeAndAfterEach
  with BeforeAndAfterAll
  with Waiters {

  val eventLoopGroup = MultiThreadEventLoopGroup()
  implicit var node: LocalEventBus = _

  behavior of "LocalEventBus"

  "test" must "asd"

  it must "route an event emitted to the listeners" in {
    val w = new Waiter

    listen to[String] "test" onNext { v =>
      w(assert(v.isDefined))
      w(assertResult("attachment")(v.get))
      w.dismiss()
    }

    emit event "test" send[String] Some("attachment")

    w await timeout(300.millis)
  }

  it must "not route a message with the wrong type of attachment" in {
    val w = new Waiter
    
    node.errorHandler = {
      case _: NoRouteFoundException => w.dismiss()
      case _ => w(fail())
    }

    listen to[Int] "test" onNext w(fail())
    emit event "test" send[String] Some("attachment")

    w await timeout(300.millis)
  }

  it must "emit an event and listen for a response" in {
    val w = new Waiter

    listen to[String] "test" onNext reply(None)

    emit
      .event("test")
      .request[String, Nothing]()
      .onSuccess(_ => w.dismiss())
      .onFailure(_ => w(fail("The reply was an error.")))

    w await timeout(300.millis)
  }

  it must "remove a route to a listener when requesting to listen only once" in {
    val w = new Waiter

    listen only once to "test" onNext w.dismiss()
    emit event "test" send()

    w await timeout(300.millis)

    node errorHandler_= {
      case _: NoRouteFoundException => w.dismiss()
      case _ => w(fail())
    }

    emit event "test" send()

    w await timeout(300.millis)
  }

  it must "treat event types as covariant" in {
    val w = new Waiter

    node.errorHandler = _ => w(fail())

    listen only once to[Any] "test" onNext w.dismiss()
    emit event "test" send[String] Some("")

    w await timeout(300.millis)
  }

  it must "route an event to all registered listeners" in {
    val w = new Waiter

    for (_ <- 0 to 3) listen to "test" onNext w.dismiss()
    emit event "test" broadcast()

    w await (timeout(300.millis), dismissals(3))
  }

  it must "correctly remove a listener" in {
    val w = new Waiter

    val listener = listen to "test" onNext w(fail())

    node
      .unroute("test", listener)
      .onSuccess(_ => w.dismiss())

    w await timeout(300.millis)

    node.errorHandler = {
      case _: NoRouteFoundException => w.dismiss()
      case _ => fail()
    }

    emit event "test" send()

    w await timeout(300.millis)
  }

  it must "forward exceptions thrown in a message handler to the error handler" in {
    val w = new Waiter

    node.errorHandler = {
      case _: RuntimeException => w.dismiss()
    }

    listen to[Nothing] "test" onNext { _ =>
      throw new RuntimeException()
    }

    emit event "test" send()

    w await timeout(300.millis)
  }

  it must "not execute an individual listener instance in parallel" in {
    val w = new Waiter

    new Listener[Int] {
      val concurrentFlag = new AtomicBoolean(false)

      listen only once to "sleep"
      listen only once to "test"

      override def onNext(value: Option[Int]): Unit = {
        w { assert(concurrentFlag.compareAndSet(false, true)) }

        if (context.currentEvent == "sleep")
          Thread.sleep(500)

        w { assert(concurrentFlag.compareAndSet(true, false)) }
        w.dismiss()
      }
    }

    emit event "sleep" send[Int] Some(500)
    emit event "test" send[Nothing] None

    w await (timeout(1000.millis), dismissals(2))
  }

  it must "route multiple events to different listener instances" in {
    val w = new Waiter

    val complete = new AtomicBoolean(false)

    listen to "test" onNext {
      w { assert(complete.compareAndSet(false, true)) }
      w.dismiss()
    }

    listen to "test" onNext {
      w.dismiss()
    }

    emit event "test" send()
    emit event "test" send()

    w await (timeout(300.millis), dismissals(2))
  }

  override protected def beforeEach(): Unit = node = LocalEventBus(eventLoopGroup)
}
