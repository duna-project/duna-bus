package io.duna.eventbus

import java.util.concurrent.ForkJoinPool

import scala.concurrent.{Future, Promise}
import scala.util.Try

import org.scalatest.{Assertion, AsyncFlatSpec, Matchers}

import io.duna.concurrent.eventExecutionContext

class EventBusSpec extends AsyncFlatSpec with Matchers {

  val node = new LocalEventBus(ForkJoinPool.commonPool())
  node.start()

  behavior of "EventBus"

  it should "route an event emitted to the correct listener" in {
    val promise = Promise[Assertion]()

    (node listenTo "test").
      onReceive { m: Option[String] =>
        promise.complete(Try(assert(m.isDefined && m.get == "attachment")))
      }

    node emit "test" dispatch Some("attachment")

    promise.future
  }

  it should "reject a message with the wrong type of attachment" in {
    val promise = Promise[Boolean]()

    val listener = node listenTo "test"
    listener onReceive { _ =>
      promise.complete(Try(false))
    } onError { _ =>
      promise.complete(Try(true))
    }

    node emit "test" dispatch Some("attachment")

    for {
      result <- promise.future
    } yield assert(result)
  }

  it should "emit an event and listen for a response" in {
    Future {
      assert(false)
    }
  }

  it should "cache messages when a subscriber is present, but without handlers" in {
    Future {
      assert(false)
    }
  }
}
