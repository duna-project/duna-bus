package io.duna.eventbus

import java.util.concurrent.ForkJoinPool
import scala.concurrent.Promise
import scala.util.Try

import io.duna.concurrent.EventLoopGroup
import org.scalatest.{Assertion, AsyncFlatSpec, Matchers}

class EventBusSpec extends AsyncFlatSpec with Matchers {

  val eventBus = new LocalEventBus(new EventLoopGroup, ForkJoinPool.commonPool())

  behavior of "EventBus"

  it should "route an event emmitted to the correct subscriber" in {
    val promise = Promise[Assertion]()

    val subscriber = eventBus.subscribeTo[String]("test")
    subscriber onReceive(m => {
      promise.complete(Try(assert(m.isDefined && m.get == "test")))
    })

    eventBus emit[String] "test"

    promise.future
  }
}
