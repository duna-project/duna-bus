//package io.duna.eventbus
//
//import scala.concurrent.{Future, Promise}
//import scala.util.Try
//
//import io.duna.eventbus2.LocalEventBus
//import org.scalatest.{Assertion, AsyncFlatSpec, Matchers}
//
//class EventBusSpec extends AsyncFlatSpec with Matchers {
//
//  val eventBus = null // new LocalEventBus
//
//  behavior of "EventBus"
//
//  it should "route an event emmitted to the correct subscriber" in {
//    val promise = Promise[Assertion]()
//
//    val subscriber = eventBus.subscribe[String]("test")
//    subscriber onNext(m => {
//      promise.complete(Try(assert(m.attachment.isDefined && m.attachment.get == "test")))
//    })
//
//    eventBus emit[String] "test"
//
//    promise.future
//  }
//}
