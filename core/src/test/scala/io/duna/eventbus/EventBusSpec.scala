package io.duna.eventbus

import java.util.concurrent.{Executors, ForkJoinPool}
import scala.concurrent.{Future, Promise}
import scala.util.Try

import org.scalatest._
import io.duna.concurrent.eventExecutionContext

class EventBusSpec extends AsyncFlatSpec
  with BeforeAndAfterEach
  with BeforeAndAfterAll {

  val node: LocalEventBus = new LocalEventBus(Executors.newFixedThreadPool(1))

  behavior of "EventBus"

  it should "route an event emitted to the correct listener" in {
    val promise = Promise[Assertion]()

    (node listenTo[String] "test").
      onReceive { m =>
        promise.complete(Try(assert(m.isDefined && m.get == "attachment")))
      }

    node emit "test" dispatch Some("attachment")

    promise.future
  }

  it should "reject a message with the wrong type of attachment" in {
    val promise = Promise[Boolean]()

    val listener = node listenTo[Int] "test"
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
    val promise = Promise[Assertion]()

    import dsl._

    node listenTo[Int] "test" onReceive { _ =>
      replyWith(Some("response"))
    }

    node.emit[Int]("test").
      expect[String](reply).
      onReceive(_ => promise.complete(Try(assert(true)))).
      dispatch(Some(1))

    promise.future
  }

  it should "cache messages when a subscriber is present, but without handlers" in {
    val promise = Promise[Assertion]()

    val listener = node listenTo[String] "test"
    node emit "test" dispatch Some("attachment")

    listener onReceive { m =>
      promise.complete(Try(assert(m.isDefined && m.get == "attachment")))
    }

    promise.future
  }


  override protected def beforeAll(): Unit = node.start()

  override protected def beforeEach(): Unit = node.removeAll("test")

  override protected def afterAll(): Unit = node.shutdown()
}
